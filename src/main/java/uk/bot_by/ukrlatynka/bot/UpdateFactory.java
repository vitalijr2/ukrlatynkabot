package uk.bot_by.ukrlatynka.bot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface UpdateFactory {

  @Nullable Update parseUpdate(@NotNull String updateText);

}
