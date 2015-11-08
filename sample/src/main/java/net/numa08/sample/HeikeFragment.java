package net.numa08.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.numa08.sample.models.Heike;

public class HeikeFragment extends Fragment {

    Heike heike;

    public static HeikeFragment newInstance(Heike heike) {
        final Bundle args = new Bundle();
        args.putParcelable("heike", heike);
        final HeikeFragment fragment = new HeikeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        heike = args.getParcelable("heike");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_heike, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView) view.findViewById(R.id.name)).setText(heike.getName());
        ((ImageView) view.findViewById(R.id.main_image)).setImageDrawable(ContextCompat.getDrawable(getContext(), heike.getImageRes()));
    }
}
