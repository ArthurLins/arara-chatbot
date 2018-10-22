package me.arthurlins.simbot.core.answers;

import gnu.trove.map.hash.THashMap;
import me.arthurlins.simbot.core.answers.exceptions.NoHaveRotaroyContextException;
import me.arthurlins.simbot.core.context.Context;
import me.arthurlins.simbot.core.context.Contexted;
import me.arthurlins.simbot.core.context.SessionRotatoryContext;
import me.arthurlins.simbot.core.tools.Configuration;
import me.arthurlins.simbot.core.tools.TextProcessor;
import me.arthurlins.simbot.pool.ThreadPool;
import me.arthurlins.simbot.storage.api.SimbotAnswerDao;
import me.arthurlins.simbot.storage.impl.SimbotAnswerDaoImp2;
import me.arthurlins.simbot.storage.impl.SimbotAnswerDaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class AnswersManager {

    private static final int DIFFERENT_FACTOR = Integer.parseInt(Configuration.getInstance().get("differentFactor"));
    private static final int CONTEXT_SIZE = Integer.parseInt(Configuration.getInstance().get("contextSize"));
    private static final int EXPIRE_TIME = 10000;


    private static AnswersManager instance;
    public static AnswersManager getInstance(){
        if (instance == null){
            instance = new AnswersManager();
        }
        return instance;
    }

    public static void initialize(){
        if (instance == null){
            instance = new AnswersManager();
        }
    }

    private Logger logger;
    private final SimbotAnswerDao simbotAnswerDao;
    private final THashMap<String, QA> answers;
    //private final List<String> history = new ArrayList<>();
    private final THashMap<String, SessionRotatoryContext> userContext;
    private final List<Contexted<String>> globalRandomTriggers;
    private final List<SessionTrigger> sessionRandomTriggers;
    private final ConcurrentHashMap<String, Contexted<String>> waitingResponse;
    //private final RotatoryHistory history = new RotatoryHistory(5);


    private AnswersManager(){
        this.logger = LoggerFactory.getLogger(AnswersManager.class);
        this.simbotAnswerDao = new SimbotAnswerDaoImpl();
        this.answers = new THashMap<>();
        this.userContext = new THashMap<>();
        this.globalRandomTriggers = new ArrayList<>();
        this.sessionRandomTriggers = new ArrayList<>();
        this.waitingResponse = new ConcurrentHashMap<>();

        loadAnswers();
        clearJob();
    }

    private void clearJob() {
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleWithFixedDelay(()->{
            waitingResponse.forEach((s, stringContexted) -> {
                if (isExpired(stringContexted.getData())){
                    waitingResponse.remove(s,stringContexted);
                    logger.info("Removendo: " + stringContexted.getT());
                }
            });
        },0, 1, TimeUnit.SECONDS);
    }

    private void loadAnswers(){
        //System.out.print("Loading answers... ");
        logger.info("Loading answers... ");
        simbotAnswerDao.getAll().forEach((qaEntry)-> {
            addAnswer(qaEntry.getQuestion(), qaEntry.getResponse(), qaEntry.getContext());
        });

    }


    private void store(String original, String response, Session session, Context context){
        addAnswer(original, response, context);
        //ThreadPool.async(()->{
            storeQAModel(new QAEntry(original, response, session, context));
        //});
    }

    public Response getResponse(String message, Session session, boolean readOnly){
        addContextMessageToSession(message, session);
        final Context context = userContext.get(session.toString()).getContext();
        if (waitingResponse.containsKey(session.toString()) && !readOnly){
            Contexted<String> getted = waitingResponse.get(session.toString());
            final String original = getted.getT();
            if (!original.equals(message) && !isExpired(getted.getData())){
                store(original, message, session, context);
                logger.info("Learned Question: "+ original + " Response: "+ message);
            }

            final Contexted<String> natural = waitingResponse.get(session.toString());

            if(globalRandomTriggers.removeIf((t)-> natural.getT().equals(t.getT()))){
                logger.info("Removed trigger from global context");
            }

            if(sessionRandomTriggers.removeIf((t)-> natural.getT().equals(t.getTrigger().getT()))){
                logger.info("Removed session trigger from global context");
            }
            waitingResponse.remove(session.toString());
        }
        if (new Random().nextInt(100) <= DIFFERENT_FACTOR && !readOnly){
            inject(context, session);
        }
        try {
            return execute(message, session, readOnly);
        } catch (NoHaveRotaroyContextException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public void getResponseString(String message, Session session, Consumer<String> response) {
//        response.accept(this.getResponse(message, session).getText());
//    }
//
//    public void getResponse(String message, Session session, Consumer<Response> response) {
//        response.accept(this.getResponse(message, session));
//    }

    private boolean isExpired(Date date){
        return (date.getTime()+EXPIRE_TIME) <= Date.from(Instant.now()).getTime();
    }

    private void inject(Context context, Session session) {
        if (answers.isEmpty()){
            return;
        }
        QA existent;
        //Collecting best QA for user session

        List<Map.Entry<String, QA>> listOfBestQA = answers.entrySet().parallelStream()
                .filter(stringQAEntry -> {
                    if (stringQAEntry.getValue().getContext() == null){
                        return false;
                    }
                    final int med = Math.abs((context.size() + stringQAEntry.getValue().getContext().size()) / 2);
                    return stringQAEntry.getValue().getContext().compare(context) >= med;
                })
                .collect(Collectors.toList());


        if(!listOfBestQA.isEmpty()){
            existent = listOfBestQA.get(random(listOfBestQA.size())).getValue();

            sessionRandomTriggers.add(new SessionTrigger(new Contexted<>(context, existent.getRandomQuestion()), session));
            logger.info("Inject session QA: ("+existent.getRandomQuestion()+")");
        } else {
            List<String> rd = new ArrayList<>(answers.keySet());
            existent = answers.get(rd.get(random(rd.size())));
            logger.info("Inject random QA: ("+existent.getRandomQuestion()+")");
        }
        globalRandomTriggers.add(new Contexted<>(context, existent.getRandomQuestion()));
    }

    private Response execute(String rawMessage, Session session, boolean readOnly) throws NoHaveRotaroyContextException {
        Context userContext = this.userContext.get(session.toString()).getContext();
        if (userContext == null){
            logger.error("No have session rotatory context to process message.");
            throw new NoHaveRotaroyContextException();
        }
        final String trigger = TextProcessor.processString(rawMessage);
        final String realTrigger = triggerMatch(trigger);
        if (realTrigger != null){
            final QA ans = answers.get(realTrigger);
            String answer = ans.getRandomResponse();

            //Todo:: id

            return new Response(UUID.randomUUID(), session, rawMessage, answer, 1);
        }

        if (TextProcessor.isValid(rawMessage)){
            if (!readOnly)
                globalRandomTriggers.add(new Contexted<>(userContext, rawMessage));
        }


        String triggerForWaiting;

        List<SessionTrigger> userTriggers = sessionRandomTriggers
                .parallelStream()
                .filter(dt->dt.getSession().toString().equals(session.toString()))
                .collect(Collectors.toList());

        if (!userTriggers.isEmpty()){
            triggerForWaiting = getRandomInSessionTrigger(userTriggers);
            logger.info("Getting random session context ("+triggerForWaiting+")");
        } else {
            triggerForWaiting = getRandomInContextTrigger(rawMessage);
            logger.info("Getting random trigger in global context");
        }

        if (triggerForWaiting == null){
            logger.info("Getting random trigger");
            triggerForWaiting = getRandomTrigger();
        }
        if(!readOnly)
            waitingResponse.put(session.toString(), new Contexted<>(userContext, triggerForWaiting));
        //Todo:: implement id
        return new Response(UUID.randomUUID(), session, rawMessage, triggerForWaiting, 1);


    }

//    private String getAndSetToGlobalScopeResponse(){
//
//    }


    private String triggerMatch(String trigger){
        if (answers.containsKey(trigger)){
            return trigger;
        }
        for (String realTrigger: answers.keySet()){
            if (TextProcessor.compare(trigger, realTrigger, 1)){
                return realTrigger;
            }
        }
        return null;
    }

    private void addContextMessageToSession(String message, Session session){
        SessionRotatoryContext src = userContext.get(session.toString());
        if (src != null){
            src.add(message);
            return;
        }
        src = new SessionRotatoryContext(CONTEXT_SIZE);
        src.add(message);
        userContext.put(session.toString(), src);
    }

    //Todo: Make comparision by contexts - OK
    private String getRandomInContextTrigger(String phrase){
        for (Contexted<String> contextedString : globalRandomTriggers){
            if (contextedString.getContext().inContext(phrase, 3) && !contextedString.getT().equals(phrase)){
                final String string = contextedString.getT();
                logger.info("Encountered best random trigger for phrase ("+ phrase+") is ("+string+")");
                return string;
            }
        }
        //logger.info("Using random trigger for phrase ("+ phrase+")");
        return null;
    }

    private String getRandomInSessionTrigger(List<SessionTrigger> userTriggers){
        if (userTriggers.isEmpty()){
            return null;
        }
        final SessionTrigger  st = (SessionTrigger) randomListElement(userTriggers);
        return st.getTrigger().getT();
    }

    private String getRandomTrigger(){
        return globalRandomTriggers.get(random(sessionRandomTriggers.size())).getT();
    }

    private void storeQAModel(QAEntry qaEntry){
        simbotAnswerDao.addQAModel(qaEntry);
    }

    private void addAnswer(String text, String response, Context context){
        String trigger = TextProcessor.processString(text);
        if (answers.containsKey(trigger)){
            answers.get(trigger).addResponse(response);
        } else {
            answers.put(trigger, new QA(1,text, response, context));
        }
    }

    private Object randomListElement(List<?> list){
        return list.get(random(list.size()));
    }

    private int random(int bound){
        if (bound == 0){
            return 0;
        }
        int rIndex = new Random().nextInt(bound);
        rIndex = (rIndex < 0)? 0: rIndex;
        return rIndex;
    }

    public int total(){
        return answers.size();
    }

}
