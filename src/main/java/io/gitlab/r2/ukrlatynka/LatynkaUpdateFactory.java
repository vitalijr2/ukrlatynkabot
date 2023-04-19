/*
 * Copyright 2023 Witalij Berdinskich
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
package io.gitlab.r2.ukrlatynka;

import static io.gitlab.r2.telegram_bot.TelegramUtils.getId;

import org.json.JSONObject;
import io.gitlab.r2.telegram_bot.AbstractUpdateFactory;
import io.gitlab.r2.telegram_bot.Update;

public class LatynkaUpdateFactory extends AbstractUpdateFactory {

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

}
