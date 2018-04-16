package app.queuena;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class messageTabFragment extends Fragment{
    private static final String TAG = "MessageTabFragment";

    private ImageButton btnSend;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_tab_fragment, container, false);
        btnSend = view.findViewById(R.id.btnSendMessage);

        return view;
    }
}
