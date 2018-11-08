package ru.glavkniga.gklients.mailing;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.glavkniga.gklients.schedule.OnetimeMailingSenderMBean;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by 4derg on 30.06.2017.
 */
@Component
public class TemplateProcessorImpl implements TemplateProcessor {

    @Inject
    private MailTokenProvider provider;

    @Inject
    private static org.slf4j.Logger log = LoggerFactory.getLogger(OnetimeMailingSenderMBean.class);


    @Override
    public boolean validate(final String template) {

        final Offsets tokensOffsets = new Offsets();

        if (template == null) {
            throw new IllegalArgumentException("Null string passed as parameter");
        }

        if (!isBalanced(template, tokensOffsets)) {
            return false;
        }

        // In case of empty or token free template
        final int[] offsets = tokensOffsets.toArray();
        if (offsets == null) {
            return true;
        }

        final List<String> rawTokens = extractTokens(template, offsets);
        final List<ParsedToken> tokens = parseTokens(rawTokens);

        if (rawTokens.size() != tokens.size()) {
            return false;
        }

        final List<String> invalidTokens = provider.findInvalid(tokens);

        return invalidTokens == null || invalidTokens.isEmpty();
    }

    @Override
    public String process(final String template, final Context context) {

        final Offsets tokensOffsets = new Offsets();

        final boolean isBalanced = isBalanced(template, tokensOffsets);
        if (!isBalanced) {
            throw new IllegalArgumentException("Email tem0plate not balanced");
        }

        final int[] offsets = tokensOffsets.toArray();
        if (offsets == null) {
            return template;
        }

        if (offsets.length % 2 != 0) {
            throw new IllegalArgumentException("Email template not balanced");
        }

        final List<String> rawTokens = extractTokens(template, offsets);
        final List<ParsedToken> tokens = parseTokens(rawTokens);

        final Map<String, String> values = provider.provide(tokens, context);
        checkValues(rawTokens, tokens, values);

        return process(template, tokensOffsets, rawTokens, tokens, values);
    }

    // TODO Rewrite with string builder or something
    private static String process(String template,
                                  final Offsets tokensOffsets,
                                  final List<String> rawTokens,
                                  final List<ParsedToken> tokens,
                                  final Map<String, String> values) {

        // TODO Switch to use tokensOffsets instead String::replace
        for (int i = 0; i < rawTokens.size(); i++) {
            final String rawToken = rawTokens.get(i);
            final ParsedToken token = tokens.get(i);
            String value = values.get(token.getToken());
            if (value == null || value.equals("")) {
                value = token.getDefaultValue();
            }
            template = template.replace(rawToken, value);
        }

        return template;
    }

    private static void checkValues(final List<String> rawTokens, final List<ParsedToken> tokens,
                                    final Map<String, String> values) {

        if (rawTokens == null || tokens == null || values == null) {
            throw new IllegalArgumentException("Not enough params to invoke process");
        }

        if (rawTokens.size() != tokens.size()) {
            throw new IllegalStateException("Token without name detected");
        }

        List<String> badTokens = null;

        for (int i = 0; i < rawTokens.size(); i++) {
            final ParsedToken token = tokens.get(i);
            final String value = values.get(token.getToken());
            if (value == null && token.getDefaultValue() == null) {
                if (badTokens == null) {
                    badTokens = new ArrayList<>();
                }
                badTokens.add(rawTokens.get(i));
            }
        }

        if (badTokens != null) {
            log.warn("Tokens without values in template: " + String.join(", ", badTokens));
            throw new TemplateProcessorException("Tokens without values in template", badTokens);
        }
    }

    private static List<ParsedToken> parseTokens(final List<String> rawTokens) {
        if (rawTokens == null) {
            return null;
        }

        final List<ParsedToken> tokens = new ArrayList<>(rawTokens.size());
        for (final String token : rawTokens) {
            // Maybe we need to have checks here or use regexp, cuz now it looks like a shit
            if (token == null || token.length() <= 3) {
                continue;
            }
            final ParsedToken parsedToken = ParsedToken.parse(token.substring(2, token.length() - 1).trim());
            if (parsedToken == null) {
                continue;
            }
            tokens.add(parsedToken);
        }
        return tokens;
    }

    private static List<String> extractTokens(final String template, final int[] offsets) {

        if (template == null || template.isEmpty()) {
            throw new IllegalArgumentException("Email template not empty");
        }

        if (offsets == null || offsets.length == 0) {
            throw new IllegalArgumentException("Offsets array is empty");
        }

        final Set<String> tokens = new HashSet<>(); // We need to avoid duplicates here

        for (int i = 0; i < offsets.length; i += 2) {
            final int begin = offsets[i];
            final int end = offsets[i + 1];
            tokens.add(template.substring(begin, end + 1));
        }

        return new ArrayList<>(tokens);
    }

    private static boolean isBalanced(final String value, final Offsets offsets) {
        final Stack<String> stack = new Stack<>();
        final int length = value.length();

        for (int i = 0; i < length; i++) {
            final char symb = value.charAt(i);
            final char nextSymb = (i < length - 1) ? value.charAt(i + 1) : '\0';

            if (symb == '$' && nextSymb == '{') {
                if (stack.isEmpty()) {
                    stack.push("${");
                    if (offsets != null) {
                        offsets.add(i);
                    }
                    continue;
                }
                return !stack.pop().equals("${");
            }

            if (symb == '}') {
                if (stack.isEmpty() || !stack.pop().equals("${")) {
                    return false;
                }
                if (offsets != null) {
                    offsets.add(i);
                }
            }
        }

        return stack.isEmpty();
    }

    private static class Offsets {
        private final List<Integer> array = new ArrayList<>();

        public void add(final int offset) {
            final int currentSize = array.size();

            if (currentSize == 0 || (currentSize > 0 && array.get(currentSize - 1) != offset)) {
                array.add(offset);
            }
        }

        private int[] toArray() {
            if (array.isEmpty()) {
                return null;
            }

            final int[] result = new int[array.size()];
            for (int i = 0; i < array.size(); i++) {
                result[i] = array.get(i);
            }
            return result;
        }

    }
}


