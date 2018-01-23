package kasper.android.file_explorer.activities;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.eightbitlab.supportrenderscriptblur.SupportRenderScriptBlur;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.WeatherConfig;
import com.survivingwithandroid.weather.lib.client.volley.WeatherClientDefault;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.model.City;
import com.survivingwithandroid.weather.lib.model.CurrentWeather;
import com.survivingwithandroid.weather.lib.model.WeatherForecast;
import com.survivingwithandroid.weather.lib.provider.IWeatherProvider;
import com.survivingwithandroid.weather.lib.provider.WeatherProviderFactory;
import com.survivingwithandroid.weather.lib.provider.openweathermap.OpenweathermapProviderType;
import com.survivingwithandroid.weather.lib.provider.wunderground.WeatherUndergroundProviderType;
import com.survivingwithandroid.weather.lib.provider.yahooweather.YahooProviderType;

import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import kasper.android.file_explorer.R;
import kasper.android.file_explorer.adapters.PagerAdapter;
import kasper.android.file_explorer.fragments.AppsFragment;
import kasper.android.file_explorer.fragments.DesktopFragment;
import kasper.android.file_explorer.fragments.DocsFragment;
import kasper.android.file_explorer.fragments.FilesFragment;
import kasper.android.file_explorer.fragments.NotesFragment;
import kasper.android.file_explorer.fragments.StorageFragment;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    Fragment[] pageFragments;
    String[] pageTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initViews();
        this.initPages();
    }

    private void initViews() {
        tabLayout = findViewById(R.id.activity_main_tab_layout);
        viewPager = findViewById(R.id.activity_main_view_pager);
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
                "",
                "",
                ""
        };
        this.viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), pageTitles, pageFragments));
        this.tabLayout.setupWithViewPager(this.viewPager);
        this.tabLayout.getTabAt(0).setIcon(R.drawable.photo_dark);
        this.tabLayout.getTabAt(1).setIcon(R.drawable.music_dark);
        this.tabLayout.getTabAt(2).setIcon(R.drawable.ic_video);
        /*this.tabLayout.getTabAt(0).setIcon(R.drawable.ic_tasks);
        this.tabLayout.getTabAt(1).setIcon(R.drawable.ic_workspace);
        this.tabLayout.getTabAt(2).setIcon(R.drawable.document_light);
        this.tabLayout.getTabAt(3).setIcon(R.drawable.ic_storage);
        this.tabLayout.getTabAt(4).setIcon(R.drawable.ic_apps);*/
        for (int counter = 0; counter < this.tabLayout.getTabCount(); counter++) {
            DrawableCompat.setTint(this.tabLayout.getTabAt(counter).getIcon(), Color.WHITE);
        }
        this.viewPager.setCurrentItem(0);
    }
}
