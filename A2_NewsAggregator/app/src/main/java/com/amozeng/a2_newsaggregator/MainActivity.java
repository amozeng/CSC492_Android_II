package com.amozeng.a2_newsaggregator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amozeng.a2_newsaggregator.APIs.Article;
import com.amozeng.a2_newsaggregator.APIs.ArticleFragment;
import com.amozeng.a2_newsaggregator.APIs.DataContainer;
import com.amozeng.a2_newsaggregator.APIs.Source;
import com.amozeng.a2_newsaggregator.APIs.SourceSelector;
import com.amozeng.a2_newsaggregator.Loader.ArticleLoader;
import com.amozeng.a2_newsaggregator.Loader.SourceLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Drawer
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private HashMap<String, Integer> colorDrawerMap = new HashMap<>();

    // ViewPager
    private MediaPageAdapter pageAdapter;
    private List<Fragment> fragments;
    private ViewPager pager;

    // Menu
    private Menu opt_menu;
    private SubMenu menu_topics;
    private SubMenu menu_countries;
    private SubMenu menu_languages;
    private SourceSelector selector = new SourceSelector();
    private static String submenu_topics = "Topics";
    private static String submenu_country = "Countries";
    private static String submenu_language = "Languages";
    public static String menu_all = "All";

    // Color Menu
    private HashMap<String, Integer> colorMenu = new HashMap<>();
    private ArrayList<Integer> colors = new ArrayList<>();

    // API
    private List<Source> sourceList = new ArrayList<>();
    private List<String> topicList = new ArrayList<>();
    private List<String> displayMediaNames = new ArrayList<>();
    private List<String> allMediaNames = new ArrayList<>();
    private ArrayList<String> countryNameList = new ArrayList<>();
    private ArrayList<String> languageNameList = new ArrayList<>();

    // State to save
    private String currentMediaName;
    private String currentMediaID = "";

    // data from JSON
    private HashMap<String, String> countryMap;
    private HashMap<String, String> languageMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make sample items for menu
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.drawer_list);


        // Set up the drawer item click callback method
        mDrawerList.setOnItemClickListener(
                (parent, view, position, id) -> {
                    selectDrawerItem(position);
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
        );

        // Create the drawer toggle
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        );

        // load json files from raw folder
        DataContainer.loadData(this);
        countryMap = new HashMap<>(DataContainer.getCountryMap());
        languageMap = new HashMap<>(DataContainer.getLanguageMap());

        // pager
        fragments = new ArrayList<>();

        pageAdapter = new MediaPageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);

        if(sourceList.isEmpty()) {
            new Thread(new SourceLoader(this)).start();
        }

        // setup colorMenu
        addAllColors(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString("TOPIC", selector.getTopic());
        outState.putString("COUNTRY", selector.getCountry());
        outState.putString("LANGUAGE", selector.getCountry());
        outState.putStringArrayList("MediaList", (ArrayList<String>) displayMediaNames);
        outState.putString("MEDIA_ID", currentMediaID);
        outState.putString("MEDIA_NAME", currentMediaName);

        // Call super last
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // Call super first
        super.onRestoreInstanceState(savedInstanceState);

        displayMediaNames = savedInstanceState.getStringArrayList("MediaList");
        selector.setTopic(savedInstanceState.getString("TOPIC"));
        selector.setCountry(savedInstanceState.getString("COUNTRY"));
        selector.setLanguage(savedInstanceState.getString("LANGUAGE"));
        setupDrawerItemColor();

        currentMediaID = savedInstanceState.getString("MEDIA_ID");
        new Thread(new ArticleLoader(this, currentMediaID)).start();

        currentMediaName = savedInstanceState.getString("MEDIA_NAME");
        setTitle(currentMediaName);

    }

    public void setupDrawerItemColor() {
        for(int i = 0; i < sourceList.size(); i++) {
            Source source = sourceList.get(i);
            String sourceTopic = source.getCategory();
            for (String key: colorMenu.keySet()) {
                String menuTopic = key;
                int menuColor = colorMenu.get(key);
                if(sourceTopic.equals(menuTopic)) {
                    //source.setColor(menuColor);
                    String sourceName = source.getName();
                    colorDrawerMap.put(sourceName, menuColor);
                    break;
                }
            }
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this,   // <== Important!
                R.layout.drawer_item, displayMediaNames){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);
                TextView currentTextView = ((TextView)view.findViewById(R.id.text_view));
                String currentMediaName = currentTextView.getText().toString();
                int colorInt;
                if(colorDrawerMap.containsKey(currentMediaName)){
                    colorInt = colorDrawerMap.get(currentMediaName);
                    currentTextView.setTextColor(colorInt);
                }
                return view;
            };
        };

        mDrawerList.setAdapter(arrayAdapter);
        ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();

    }

    private void selectDrawerItem(int position) {
        pager.setBackground(null);
        String currentMediaName = displayMediaNames.get(position);
        this.currentMediaName = currentMediaName;
        // get media's id
        String mediaID = "";
        for(Source s: sourceList) {
            if(s.getName().equals(currentMediaName)){
                mediaID = s.getID();
                break;
            }
        }
        if(mediaID != null) {
            Log.d(TAG, "selectDrawerItem: find mediaID");
            currentMediaID = mediaID;
            new Thread(new ArticleLoader(this, mediaID)).start();
        }else{
            Log.d(TAG, "selectDrawerItem: mediaID is null");
        }
        
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    // Drawer
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        opt_menu = menu;
        if(menu_topics == null) {
            setupSubMenu();
        }
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle" +  item);
            return true;
        }

        String selectedMenuItem = item.toString();
        processMenu(selectedMenuItem);
        return super.onOptionsItemSelected(item);
    }

    private void processMenu(String s) {
        if(s.equals(submenu_topics)){
            selector.setTopicStatus();
            return;
        }else if (s.equals(submenu_country)) {
            selector.setCountryStatus();
            return;
        }else if (s.equals(submenu_language)) {
            selector.setLanguageStatus();
            return;
        }else {
            if(selector.getTopicStatus()) selector.setTopic(s);
            if(selector.getCountryStatus()) selector.setCountry(s);
            if(selector.getLanguageStatus()) selector.setLanguage(s);
        }

        // check Topics
        String selectorTopic = selector.getTopic();
        String selectorCountry = selector.getCountry();
        String selectorLanguage = selector.getLanguage();

        boolean isTopicAll = selector.getTopic().equals("All");
        boolean isCountryAll = selector.getCountry().equals("All");
        boolean isLanguageAll = selector.getLanguage().equals("All");

        displayMediaNames.clear();


        for(int i = 0; i < sourceList.size(); i++) {
            Source source = sourceList.get(i);
            String sourceTopic = source.getCategory();
            String sourceCountryCode = source.getCountry();
            String sourceCountry = countryMap.get(sourceCountryCode.toUpperCase());
            String sourceLanguageCode = source.getLanguage();
            String sourceLanguage = languageMap.get(sourceLanguageCode.toUpperCase());
            String sourceName = source.getName();

            if(isTopicAll && isCountryAll && isLanguageAll) {
                for(int j = 0; j < allMediaNames.size(); j++) {
                    displayMediaNames.add(allMediaNames.get(j));
                }
//                displayMediaNames = allMediaNames;
                break;
            }else if(isTopicAll && isCountryAll){
                if(sourceLanguage.equals(selectorLanguage)) {
                    displayMediaNames.add(sourceName);
                }
            }else if(isTopicAll && isLanguageAll){
                if(sourceCountry.equals(selectorCountry)) {
                    displayMediaNames.add(sourceName);
                }
            }else if (isCountryAll && isLanguageAll) {
                if(sourceTopic.equals(selectorTopic)) {
                    displayMediaNames.add(sourceName);
                }
            }else if (isTopicAll) {
                if(sourceCountry.equals(selectorCountry) && sourceLanguage.equals(selectorLanguage)) {
                    displayMediaNames.add(sourceName);
                }
            }else if (isCountryAll) {
                if(sourceTopic.equals(selectorTopic) && sourceLanguage.equals(selectorLanguage)) {
                    displayMediaNames.add(sourceName);
                }
            }else if (isLanguageAll) {
                if(sourceTopic.equals(selectorTopic) && sourceCountry.equals(selectorCountry))
                    displayMediaNames.add(sourceName);
            }else { // all selected
                if(sourceTopic.equals(selectorTopic) && sourceCountry.equals(selectorCountry) && sourceLanguage.equals(selectorLanguage) ) {
                    displayMediaNames.add(sourceName);
                }
            }
        }
        selector.finishSelecting();

        // TODO no result alert
        if(displayMediaNames.size() < 1) {
            noResultAlert(selectorTopic, selectorCountry, selectorLanguage);
        }

        ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
        setTitle("News Gateway (" + displayMediaNames.size() + ")");
    }


    private void noResultAlert(String topic, String country, String language) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setTitle("No Sources");
        String message = "No News Sources match your criteria: \n" +
                "\nTopic: " + topic +
                "\nCountry: " + country +
                "\nLanguage: " + language;
        builder.setMessage(message);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    // get data from runnable
    public void getSources(List<Source> sources) {
        if(sources == null) {
            Log.d(TAG, "getSources: sources is null" );
            return;
        }

        sourceList = sources;
    }

    public void setupTopicList(List<String> _categoryList) {
        if(_categoryList == null) return;

        topicList = _categoryList;
    }

    private void setupSubMenu() {
        if(menu_topics == null) {
            menu_topics = opt_menu.addSubMenu(submenu_topics);
            menu_countries = opt_menu.addSubMenu(submenu_country);
            menu_languages = opt_menu.addSubMenu(submenu_language);
            menu_topics.add(menu_all);
            menu_countries.add(menu_all);
            menu_languages.add(menu_all);
        }

        // setup topics in menu
        int colorCounter = 0;

        for(int i = 0; i < topicList.size(); i++) {
            String category = topicList.get(i);

            MenuItem currentTopic = menu_topics.add(category);

            if(colors.get(colorCounter) == null) {
                colorCounter = 0;
            }

            int colorID = colors.get(colorCounter);

            colorMenu.put(category, colorID);

            // set color
            SpannableString s = new SpannableString(category);
            s.setSpan(new ForegroundColorSpan(colorID), 0, s.length(), 0);
            //s.setSpan(new ForegroundColorSpan();

            currentTopic.setTitle(s);

            colorCounter++;

        }

        // setup country in menu
        for(String country : countryNameList) {
            menu_countries.add(country);
        }

        // setup languages in menu
        for(String languageName : languageNameList){
            menu_languages.add(languageName);
        }

    }

    public void setupCountryList(List<String> conList) {
        if (conList == null) return;

        for (int i = 0; i < conList.size(); i++) {
            String countryCode = conList.get(i).toUpperCase();
            if(countryMap.containsKey(countryCode)) {
                String countryName = countryMap.get(countryCode);
                countryNameList.add(countryName);
            }
        }
        Collections.sort(countryNameList);
    }

    public void setupLanguageList(List<String> lanList) {
        if (lanList == null)  return;

        for(int i = 0; i < lanList.size(); i++) {
            String languageCode = lanList.get(i).toUpperCase();
            if(languageMap.containsKey(languageCode)) {
                String languageName = languageMap.get(languageCode);
                languageNameList.add(languageName);
            }
        }

        Collections.sort(languageNameList);

        if(opt_menu != null){
            setupSubMenu();
        }

    }

    public void setupNameList(List<String> nameList) {
        if(nameList == null) return;
        if(displayMediaNames.isEmpty()){
            displayMediaNames = nameList;
        }
        for(int i = 0; i < nameList.size(); i ++) {
            allMediaNames.add(nameList.get(i));
        }

        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_item, displayMediaNames));
        
        // show drawer icon
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        setTitle("News Gateway ("+ displayMediaNames.size() + ")");

    }

    public void setArticles(ArrayList<Article> articleList) {
        setTitle(currentMediaName);

        for (int i = 0; i < pageAdapter.getCount(); i++)
            pageAdapter.notifyChangeInPosition(i);

        fragments.clear();

        for (int i = 0; i < articleList.size(); i++) {
            fragments.add(
                    ArticleFragment.newInstance(articleList.get(i), i+1, articleList.size()));
        }


        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);

        if(!fragments.isEmpty()) {
            pager.setBackground(null);
        }
    }

    private void addAllColors(Context context) {

//        int[] categoryColors = context.getResources().getIntArray(R.array.category_colors);
//        for(int i = 0; i < categoryColors.length; i++)
//        {
//            int colorID = categoryColors[i];
//            colors.add(categoryColors[i]);
//            //Integer colorInt = R.color.category_0
//        }

        int color_0 = ContextCompat.getColor(context, R.color.category_0);
        int color_1 = ContextCompat.getColor(context, R.color.category_1);
        int color_2 = ContextCompat.getColor(context, R.color.category_2);
        int color_3 = ContextCompat.getColor(context, R.color.category_3);
        int color_4 = ContextCompat.getColor(context, R.color.category_4);
        int color_5 = ContextCompat.getColor(context, R.color.category_5);
        int color_6 = ContextCompat.getColor(context, R.color.category_6);
        int color_7 = ContextCompat.getColor(context, R.color.category_7);
        int color_8 = ContextCompat.getColor(context, R.color.category_8);
        int color_9 = ContextCompat.getColor(context, R.color.category_9);
        int color_10 = ContextCompat.getColor(context, R.color.category_10);



        colors.add(color_0);
        colors.add(color_1);
        colors.add(color_2);
        colors.add(color_3);
        colors.add(color_4);
        colors.add(color_5);
        colors.add(color_6);
        colors.add(color_7);
        colors.add(color_8);
        colors.add(color_9);
        colors.add(color_10);


    }

    /////////////////////////////////////////////////////////////////////////////////
    private class MediaPageAdapter extends FragmentPagerAdapter {

        private long baseId = 0;

        MediaPageAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }
    }

}

