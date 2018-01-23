package kasper.android.file_explorer.activities;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import kasper.android.file_explorer.R;
import kasper.android.file_explorer.adapters.PagerAdapter;
import kasper.android.file_explorer.fragments.DocsFragment;

public class DocsActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    Fragment[] pageFragments;
    String[] pageTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docs);

        this.initViews();
        this.initPages();
    }

    public void onBackBtnClicked(View view) {
        this.onBackPressed();
    }

    private void initViews() {
        tabLayout = findViewById(R.id.activity_docs_tab_layout);
        viewPager = findViewById(R.id.activity_docs_view_pager);
    }

    private void initPages() {
        this.pageFragments = new Fragment[]{
                new DocsFragment().setDocType("photos"),
                new DocsFragment().setDocType("musics"),
                new DocsFragment().setDocType("videos")
        };
        this.pageTitles = new String[]{
                "",
                "",
                ""
        };
        this.viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), pageTitles, pageFragments));
        this.tabLayout.setupWithViewPager(this.viewPager);
        this.tabLayout.getTabAt(0).setIcon(R.drawable.photo_dark);
        this.tabLayout.getTabAt(1).setIcon(R.drawable.music_dark);
        this.tabLayout.getTabAt(2).setIcon(R.drawable.ic_video);
        for (int counter = 0; counter < this.tabLayout.getTabCount(); counter++) {
            DrawableCompat.setTint(this.tabLayout.getTabAt(counter).getIcon(), Color.WHITE);
        }
    }
}
