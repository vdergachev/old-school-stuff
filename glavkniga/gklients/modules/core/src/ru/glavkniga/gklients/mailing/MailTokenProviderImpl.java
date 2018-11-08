package ru.glavkniga.gklients.mailing;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Transaction;
import org.springframework.stereotype.Component;
import ru.glavkniga.gklients.mailing.token.PersonalTokenEvaluator;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Vladimir on 01.07.2017.
 */
@Component
public class MailTokenProviderImpl extends BaseMailTokenProvider implements MailTokenProvider {

    private volatile Set<String> supportedTokens;

    @Override
    public List<String> findInvalid(List<ParsedToken> tokens) {

        if (tokens == null) {
            throw new IllegalArgumentException("No tokens provided");
        }

        loadTokens();

        List<String> invalid = null;
        for (final ParsedToken token : tokens) {
            final String name = token.getToken();
            if (!supportedTokens.contains(name)) {
                if (invalid == null) {
                    invalid = new ArrayList<>();
                }
                invalid.add(name);
            }
        }
        return invalid;
    }

    @Override
    public Map<String, String> provide(final List<ParsedToken> tokens, final Context context) {

        if (tokens == null) {
            throw new IllegalArgumentException("No token provided");
        }

        loadTokens();

        if (isInvalidNames(tokens)) {
            throw new IllegalArgumentException("Can't provide unsupported token(s)");
        }

        return internalProvide(tokens, context);
    }

    private void loadTokens() {
        if (supportedTokens == null) {
            synchronized (this) {
                if (supportedTokens == null) {
                    supportedTokens = loadSupportedTokens();
                    loadEvaluators();
                }
            }
        }
    }

    private Set<String> loadSupportedTokens() {
        try (Transaction tx = persistence.createTransaction()) {

            final EntityManager entityManager = persistence.getEntityManager();

            final List<String> allTokens = entityManager
                    .createQuery("select t.token from gklients$Token t", String.class)
                    .getResultList();

            tx.commit();

            return new HashSet<>(allTokens);
        }
    }

    private boolean isInvalidNames(final List<ParsedToken> tokens) {
        final List<String> tokenNames = tokens.stream().map(ParsedToken::getToken).collect(Collectors.toList());
        final Set<String> intersection = new HashSet<>(tokenNames);
        intersection.removeAll(supportedTokens);
        return !intersection.isEmpty();
    }

    private Map<String, String> internalProvide(final List<ParsedToken> names, final Context context) {
        final Map<String, String> values = new HashMap<>(names.size());
        for (final ParsedToken token : names) {
            final PersonalTokenEvaluator<Context, String> tokenEvaluatorProvider = evaluators.get(token.getToken());
            final Function<Context, String> evaluator = tokenEvaluatorProvider.getEvaluator(context);
            if (evaluator == null) {
                continue;
            }
            values.put(token.getToken(), evaluate(evaluator, context));
        }
        return values;
    }

    private String evaluate(final Function<Context, String> evaluator, final Context context) {
        try {
            return evaluator.apply(context);
        } catch (Exception ex) {
            return null;
        }
    }

}
