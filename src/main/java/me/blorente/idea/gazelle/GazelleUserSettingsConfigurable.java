package me.blorente.idea.gazelle;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.idea.blaze.base.settings.ui.BlazeUserSettingsCompositeConfigurable;
import com.google.idea.common.settings.AutoConfigurable;
import com.google.idea.common.settings.ConfigurableSetting;
import com.google.idea.common.settings.SearchableText;
import com.google.idea.common.settings.SettingComponent;
import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.ui.TextFieldWithStoredHistory;

class GazelleUserSettingsConfigurable extends AutoConfigurable {
    static class UiContributor implements BlazeUserSettingsCompositeConfigurable.UiContributor {

        @Override
        public UnnamedConfigurable getConfigurable() {
            return new GazelleUserSettingsConfigurable();
        }

        @Override
        public ImmutableCollection<SearchableText> getSearchableText() {
            return SearchableText.collect(SETTINGS);
        }
    }

    private static final ConfigurableSetting<?, ?> GAZELLE_TARGET = ConfigurableSetting.builder(GazelleUserSettings::getInstance)
            .label("Gazelle target to run on Sync.")
            .getter(GazelleUserSettings::getGazelleTarget)
            .setter(GazelleUserSettings::setGazelleTarget)
            .componentFactory(
                    SettingComponent.LabeledComponent.factory(
                            () -> new TextFieldWithStoredHistory("TEST"),
                            s -> Strings.nullToEmpty(s.getText()).trim(),
                            TextFieldWithStoredHistory::setTextAndAddToHistory
                    )
            );

    private static final ImmutableList<ConfigurableSetting<?, ?>> SETTINGS = ImmutableList.of(GAZELLE_TARGET);

    private GazelleUserSettingsConfigurable() {
        super(SETTINGS);
    }
}
