package com.amozeng.a2_newsaggregator.APIs;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.amozeng.a2_newsaggregator.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static androidx.core.content.ContextCompat.getSystemService;

public class ArticleFragment extends Fragment {

    private static final String TAG = "ArticleFragment";

    public ArticleFragment () {}

    public static ArticleFragment newInstance(Article article, int index, int max)
    {
        ArticleFragment f = new ArticleFragment();
        //Constructs a new, empty Bundle sized to hold the given number of elements. (1)
        Bundle bdl = new Bundle(1);
        bdl.putSerializable("ARTICLE_DATA", article);
        bdl.putSerializable("INDEX", index);
        bdl.putSerializable("TOTAL_COUNT", max);
        f.setArguments(bdl);

        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragment_layout = inflater.inflate(R.layout.fragment_article, container, false);
        Bundle args = getArguments();

        if (args != null) {
            final Article currentArticle = (Article) args.getSerializable("ARTICLE_DATA");
            if (currentArticle == null) {
                return null;
            }
            int index = args.getInt("INDEX");
            int total = args.getInt("TOTAL_COUNT");

            // Get the string to display from the arguments bundle

            String websiteURL = currentArticle.getUrl();

            TextView articleTitle = fragment_layout.findViewById(R.id.article_title);
            String title = currentArticle.getTitle();
            articleTitle.setText(currentArticle.getTitle());

            TextView articleDate = fragment_layout.findViewById(R.id.article_date);
            String dateStr = currentArticle.getDate();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
            Date date = null;
            String formattedDate = null;
            try {
                date = format.parse(dateStr);
                Log.d(TAG, "Date: " + date.toString()); // Sat Jan 02 00:00:00 GMT 2010

                SimpleDateFormat desiredFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH);
                formattedDate = desiredFormat.format(date);
                Log.d(TAG, "Date after formatted: " + formattedDate);

            } catch (ParseException e) {
                Log.d(TAG, "Date: wrong format" + e.getMessage());
            }

            articleDate.setText(formattedDate);

            TextView articleAuthor = fragment_layout.findViewById(R.id.article_author);
            String author = currentArticle.getAuthor();
            if(author.equals("null") || author.isEmpty()) {
                articleAuthor.setVisibility(View.GONE);
            }else{
                articleAuthor.setText(currentArticle.getAuthor());
            }

            // TODO image
            ImageView imageView = fragment_layout.findViewById(R.id.article_image);
            String imageUrl = currentArticle.getUrlToImage();
            if(imageUrl.equals("null")) {
                imageView.setImageResource(R.drawable.noimage);
            }else{
                loadImagePicasso(imageView, imageUrl);
            }

            TextView articleContent = fragment_layout.findViewById(R.id.article_content);
            articleContent.setText(currentArticle.getDescription());
            articleContent.setMovementMethod(new ScrollingMovementMethod());

            TextView pageNum = fragment_layout.findViewById(R.id.page_num);
            pageNum.setText(String.format(Locale.US, "%d of %d", index, total));

            // set onClickListener
            imageView.setOnClickListener(v -> openWebsite(websiteURL));
            articleTitle.setOnClickListener(v -> openWebsite(websiteURL));
            //articleContent.setOnClickListener(v -> openWebsite(websiteURL));

            return fragment_layout;
        }else{
            return null;
        }
    }

    private void loadImagePicasso(ImageView imageView, String imageURL){

        Picasso.get().load(imageURL).error(R.drawable.brokenimage).placeholder(R.drawable.loading).into(imageView,
                new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess: Size:" + ((BitmapDrawable) imageView.getDrawable()).getBitmap().getByteCount());
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    public void openWebsite(String websiteURL) {

        // TODO: rm these

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteURL));
        startActivity(browserIntent);

    }

}
