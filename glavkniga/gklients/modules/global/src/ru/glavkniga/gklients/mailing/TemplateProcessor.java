package ru.glavkniga.gklients.mailing;

import java.util.List;

/**
 * Created by 4derg on 30.06.2017.
 */
public interface TemplateProcessor {
    public boolean validate(final String template);

    public String process(final String template, final Context context);

    public static class TemplateProcessorException extends RuntimeException {
        private List<String> wrongTokens;

        public TemplateProcessorException(final String message, final List<String> wrongTokens) {
            super(message);
            this.wrongTokens = wrongTokens;
        }

        public TemplateProcessorException(final String message) {
            super(message);
        }

        public List<String> getWrongTokens() {
            return wrongTokens;
        }
    }
}
