package app.queuena;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class pollTabFragment extends Fragment{
    private static final String TAG = "PollTabFragment";

    private Button btnTest;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.poll_tab_fragment, container, false);
        btnTest = view.findViewById(R.id.btnPollSubmit);

        return view;
    }

}
