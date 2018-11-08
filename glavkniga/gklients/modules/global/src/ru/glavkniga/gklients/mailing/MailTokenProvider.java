package ru.glavkniga.gklients.mailing;

import java.util.List;
import java.util.Map;

/**
 * Created by Vladimir on 01.07.2017.
 */
public interface MailTokenProvider {
    List<String> findInvalid(List<ParsedToken> tokens);

    Map<String, String> provide(List<ParsedToken> tokens, Context context);
}
