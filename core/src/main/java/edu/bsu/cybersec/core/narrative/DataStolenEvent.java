/*
 * Copyright 2016 Paul Gestwicki
 *
 * This file is part of The Social Startup Game
 *
 * The Social Startup Game is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Social Startup Game is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with The Social Startup Game.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.bsu.cybersec.core.narrative;

import com.google.common.collect.ImmutableList;
import edu.bsu.cybersec.core.GameWorld;
import edu.bsu.cybersec.core.NarrativeEvent;

import java.util.List;

public class DataStolenEvent extends NarrativeEvent {

    private static final String EVENT_NAME = "DataStolen";
    private static final int HOURS_UNTIL_LOSS_ON_NOTIFY = 5;
    private static final float PERCENT_LOSS_ON_NOTIFY = 0.05f;
    private static final int HOURS_UNTIL_DISCOVERY_AFTER_IGNORE = 8;
    private static final float PERCENT_LOSS_ON_IGNORE = 0.40f;

    public DataStolenEvent(GameWorld world) {
        super(world);
    }

    @Override
    public String text() {
        return "Hackers stole some user data! What do you do?";
    }

    @Override
    public List<? extends Option> options() {
        return ImmutableList.of(new NotifyOption(), new IgnoreOption());
    }

    private class NotifyOption extends Option.Terminal {
        private final String text = "Notify our users";

        @Override
        public String eventAction() {
            return EVENT_NAME;
        }

        @Override
        public String eventLabel() {
            return "notify";
        }

        @Override
        public String text() {
            return text;
        }

        @Override
        public void onSelected() {
            super.onSelected();
            after(HOURS_UNTIL_LOSS_ON_NOTIFY).post(new AbstractUserLossEvent(world, PERCENT_LOSS_ON_NOTIFY) {
                @Override
                public List<? extends Option> options() {
                    return ImmutableList.of(new DoNothingOption("Ok"));
                }

                @Override
                public String text() {
                    return loss + " users have left our service after hearing about how hackers stole some of their personal information.";
                }
            });
        }
    }

    private class IgnoreOption extends Option.Terminal {
        private final String text = "Ignore It";

        @Override
        public String text() {
            return text;
        }

        @Override
        public String eventLabel() {
            return "ignore";
        }

        @Override
        public String eventAction() {
            return EVENT_NAME;
        }

        @Override
        public void onSelected() {
            super.onSelected();
            after(HOURS_UNTIL_DISCOVERY_AFTER_IGNORE).post(new AbstractUserLossEvent(world, PERCENT_LOSS_ON_IGNORE) {
                @Override
                public List<? extends Option> options() {
                    return ImmutableList.of(new DoNothingOption("Ok"));
                }

                @Override
                public String text() {
                    return "An independent security expert discovered that you ignored a data breach! " + loss + " users have left after discovering that you did not notify them.";
                }
            });
        }
    }
}
