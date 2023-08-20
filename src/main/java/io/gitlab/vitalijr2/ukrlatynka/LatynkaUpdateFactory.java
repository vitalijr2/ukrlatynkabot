/*
 * Copyright 2023 Vitalij Berdinskih
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gitlab.vitalijr2.ukrlatynka;

import static io.gitlab.vitalijr2.telegram_bot.TelegramUtils.getId;

import io.gitlab.vitalijr2.telegram_bot.AbstractUpdateFactory;
import io.gitlab.vitalijr2.telegram_bot.Update;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.json.JSONObject;

public class LatynkaUpdateFactory extends AbstractUpdateFactory {

  private static final String MESSAGES_PROPERTIES = "messages.properties";

  private final Properties messages;

  public LatynkaUpdateFactory() {
    this(MESSAGES_PROPERTIES);
  }

  public LatynkaUpdateFactory(String messagesProperties) {
    messages = loadMessages(messagesProperties);
  }

  @Override
  protected Update processInlineQuery(JSONObject message) {
    logger.trace("Process inline query");

    long inlineQueryId = getId(message);

    return null;
  }

  @Override
  protected Update processMessage(JSONObject message) {
    return null;
  }

  private Properties loadMessages(String messagesProperties) {
    var messages = new Properties();

    try (InputStream messageInputStream = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream(messagesProperties)) {
      messages.load(messageInputStream);
    } catch (IOException | NullPointerException exception) {
      // ignored
    }

    return messages;
  }

}
