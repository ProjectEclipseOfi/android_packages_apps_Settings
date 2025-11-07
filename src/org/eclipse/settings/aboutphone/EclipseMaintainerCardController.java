package org.eclipse.settings.aboutphone;

import android.content.Context;
import android.os.SystemProperties;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class EclipseMaintainerCardController extends AbstractPreferenceController {

    private static final String UNKNOWN = "Unknown";
    private static final String PROC_VERSION_PATH = "/proc/version";
    private LayoutPreference mLayoutPreference;

    public EclipseMaintainerCardController(Context context, String key) {
        super(context);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return "eclipse_maintainer_card";
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mLayoutPreference = screen.findPreference(getPreferenceKey());
        if (mLayoutPreference != null) {
            updateState(mLayoutPreference);
        }
    }

    @Override
    public void updateState(Preference preference) {
        if (mLayoutPreference == null) {
            return;
        }
        
        TextView maintainerView = mLayoutPreference.findViewById(R.id.maintainer_value);
        maintainerView.setText(SystemProperties.get("ro.eclipse.maintainer", UNKNOWN));
        
        TextView kernelView = mLayoutPreference.findViewById(R.id.kernel_value);
        kernelView.setText(getKernelVersion());
    }

    /**
     * get kernel version.
     */
    private String getKernelVersion() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(PROC_VERSION_PATH));
            String line;
            if ((line = reader.readLine()) != null) {
                //only get kernel version
                //
                return line.split("\\s+")[2];
            }
            reader.close();
        } catch (IOException e) {
            // e.printStackTrace();
        }
        return UNKNOWN;
    }
}
