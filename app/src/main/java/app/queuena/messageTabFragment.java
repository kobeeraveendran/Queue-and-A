package app.queuena;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class messageTabFragment extends Fragment{
    private static final String TAG = "MessageTabFragment";

    //private ChatArrayAdapter adp;
    private ListView list;
    private EditText chatMessage;
    private ImageButton btnSend;

    Intent intent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_tab_fragment, container, false);
        btnSend = view.findViewById(R.id.btnSendMessage);

        return view;
    }

}
