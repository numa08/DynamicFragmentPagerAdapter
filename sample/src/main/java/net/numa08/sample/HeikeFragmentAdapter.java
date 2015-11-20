package net.numa08.sample;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import net.numa08.dynamicfragmentstatepageradapter.support.DynamicFragmentStatePagerAdapter;
import net.numa08.sample.models.Heike;

import java.util.List;

public class HeikeFragmentAdapter extends DynamicFragmentStatePagerAdapter {

    private final List<Heike> heikeList;

    public HeikeFragmentAdapter(FragmentManager fm, List<Heike> heikeList) {
        super(fm);
        this.heikeList = heikeList;
    }

    @Override
    public Fragment getItem(int position) {
        return HeikeFragment.newInstance(heikeList.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return heikeList.get(position).getName();
    }

    @Override
    public int getCount() {
        return heikeList.size();
    }

    @Override
    public void removeItem(int position) {
        heikeList.remove(position);
        super.removeItem(position);
    }
}
