package rival.com.changecolorindicater;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by rival on 2015/11/19.
 */
public class TabFragment extends Fragment {

    private String mTitle = "Default";

    public TabFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mTitle = getArguments().getString("title");
        }

        TextView textView = new TextView(getActivity());
        textView.setTextSize(20);
        textView.setBackgroundColor(Color.parseColor("#ffffff"));
        textView.setGravity(Gravity.CENTER);
        textView.setText(mTitle);
        return textView;

    }
}
