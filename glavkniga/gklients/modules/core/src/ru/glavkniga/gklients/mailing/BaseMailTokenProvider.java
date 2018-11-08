package ru.glavkniga.gklients.mailing;

import com.google.common.collect.ImmutableMap;
import com.haulmont.cuba.core.Persistence;
import ru.glavkniga.gklients.mailing.token.PersonalTokenEvaluator;
import ru.glavkniga.gklients.service.DefineIssuesRangeService;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

/**
 * Created by vdergachev on 11.07.17.
 */
// TODO Это декомпозиция должна быть удалена в случае перехода на DSL based токены
class BaseMailTokenProvider {

    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    protected volatile Map<String, PersonalTokenEvaluator<Context, String>> evaluators;

    @Inject
    protected Persistence persistence;

    @Inject
    private DefineIssuesRangeService rangeService;

    void loadEvaluators() {
        evaluators = ImmutableMap.<String, PersonalTokenEvaluator<Context, String>>builder()
                .put("EMAIL", ctx -> ctx.client.getEmail())
                .put("NAME", ctx -> ctx.client.getName())
                .put("PASSWORD", ctx -> ctx.client.getPassword())
                .put("RIZ_NAME", ctx -> val(ctx.elverSubscription.getRiz().getMailName()))
                .put("RIZ_EMAIL", ctx -> val(ctx.elverSubscription.getRiz().getMailEmail()))
                .put("RIZ_PHONE", ctx -> val(ctx.elverSubscription.getRiz().getMailPhone()))
                .put("RIZ_PERSON", ctx -> val(ctx.elverSubscription.getRiz().getMailPerson()))
                .put("REGKEY", ctx -> val(ctx.elverSubscription.getRegkey()))
                .put("DATE_END", ctx -> frmt(rangeService.getDateTo(ctx.elverSubscription)))
                .put("DATE_START", ctx -> frmt(rangeService.getDateFrom(ctx.elverSubscription)))
                .put("NUM_END", ctx -> val(ctx.elverSubscription.getIssueEnd().getCode()))
                .put("NUM_START", ctx -> val(ctx.elverSubscription.getIssueStart().getCode()))
                .put("ABB", ctx -> val(ctx.magazineIssueStart.getCode().substring(0, 2)))
                .put("NUMBER_START", ctx -> val(ctx.magazineIssueStart.getNumber()))
                .put("NUMBER_END", ctx -> val(ctx.magazineIssueEnd.getNumber()))
                .put("YEAR_START", ctx -> val(ctx.magazineIssueStart.getYear()))
                .put("YEAR_END", ctx -> val(ctx.magazineIssueEnd.getYear()))
                .put("CODE_START", ctx -> val(ctx.magazineIssueStart.getCode()))
                .put("CODE_END", ctx -> val(ctx.magazineIssueEnd.getCode()))
                .build();
    }

//    void loadEvaluators(Boolean useDatabase) { //TODO Нешмогла :(
//        List<Token> tokens = null;
//        TypedQuery<Token> query;
//
//        Transaction tx = persistence.createTransaction();
//        try {
//            EntityManager em = persistence.getEntityManager();
//            query = em.createQuery("SELECT t FROM gklients$Token t " +
//                    "WHERE t.deleteTs is null", Token.class);
//            tokens = query.getResultList();
//            tx.commit();
//        } catch (NoResultException ignored) {
//
//        } finally {
//            tx.end();
//        }
//
//        if (tokens != null && tokens.size() >= 0) {
//             ImmutableMap.Builder<String, PersonalTokenEvaluator<Context, String>> evaluatorsMap = ImmutableMap.builder();
//
//
//            for (Token token : tokens) {
//                token.getEntityName();
//                token.getEntityField();
//                try {
//                    Class clazz = Class.forName(token.getEntityName());
//                    Method method = ReflectionUtils.findMethod(clazz, "get"+token.getEntityField());
////                    evaluatorsMap.put(token.getToken(),ctx-> val(  )); //TODO  List Context fields and
//
//                } catch (ClassNotFoundException e){
//                    e.printStackTrace(); //TODO to avoid this add class validation on Token Edit + Add Entity class selection on TokenEdit screen
//                } catch (MethodNotFoundException e){
//                    e.printStackTrace(); //TODO to avoid this add method validation on Token Edit + Add method selection on for given EntityClass TokenEdit screen
//                }
//            }
//            evaluators = evaluatorsMap.build();
//        }
//    }

    private static String val(final Object object) {
        return object != null ? object + "" : "";
    }

    private String frmt(final Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).format(DATE_TIME_FORMATTER);
    }

}
