// Generated by view binder compiler. Do not edit!
package com.google.android.ads.nativetemplates.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.ads.nativetemplates.R;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAdView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class GntMediumTemplateViewBinding implements ViewBinding {
  @NonNull
  private final View rootView;

  @NonNull
  public final TextView adNotificationView;

  @NonNull
  public final TextView body;

  @NonNull
  public final AppCompatButton cta;

  @NonNull
  public final LinearLayout headline;

  @NonNull
  public final ImageView icon;

  @NonNull
  public final MediaView mediaView;

  @NonNull
  public final NativeAdView nativeAdView;

  @NonNull
  public final TextView primary;

  @NonNull
  public final RatingBar ratingBar;

  @NonNull
  public final LinearLayout rowTwo;

  @NonNull
  public final TextView secondary;

  private GntMediumTemplateViewBinding(@NonNull View rootView, @NonNull TextView adNotificationView,
      @NonNull TextView body, @NonNull AppCompatButton cta, @NonNull LinearLayout headline,
      @NonNull ImageView icon, @NonNull MediaView mediaView, @NonNull NativeAdView nativeAdView,
      @NonNull TextView primary, @NonNull RatingBar ratingBar, @NonNull LinearLayout rowTwo,
      @NonNull TextView secondary) {
    this.rootView = rootView;
    this.adNotificationView = adNotificationView;
    this.body = body;
    this.cta = cta;
    this.headline = headline;
    this.icon = icon;
    this.mediaView = mediaView;
    this.nativeAdView = nativeAdView;
    this.primary = primary;
    this.ratingBar = ratingBar;
    this.rowTwo = rowTwo;
    this.secondary = secondary;
  }

  @Override
  @NonNull
  public View getRoot() {
    return rootView;
  }

  @NonNull
  public static GntMediumTemplateViewBinding inflate(@NonNull LayoutInflater inflater,
      @NonNull ViewGroup parent) {
    if (parent == null) {
      throw new NullPointerException("parent");
    }
    inflater.inflate(R.layout.gnt_medium_template_view, parent);
    return bind(parent);
  }

  @NonNull
  public static GntMediumTemplateViewBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.ad_notification_view;
      TextView adNotificationView = ViewBindings.findChildViewById(rootView, id);
      if (adNotificationView == null) {
        break missingId;
      }

      id = R.id.body;
      TextView body = ViewBindings.findChildViewById(rootView, id);
      if (body == null) {
        break missingId;
      }

      id = R.id.cta;
      AppCompatButton cta = ViewBindings.findChildViewById(rootView, id);
      if (cta == null) {
        break missingId;
      }

      id = R.id.headline;
      LinearLayout headline = ViewBindings.findChildViewById(rootView, id);
      if (headline == null) {
        break missingId;
      }

      id = R.id.icon;
      ImageView icon = ViewBindings.findChildViewById(rootView, id);
      if (icon == null) {
        break missingId;
      }

      id = R.id.media_view;
      MediaView mediaView = ViewBindings.findChildViewById(rootView, id);
      if (mediaView == null) {
        break missingId;
      }

      id = R.id.native_ad_view;
      NativeAdView nativeAdView = ViewBindings.findChildViewById(rootView, id);
      if (nativeAdView == null) {
        break missingId;
      }

      id = R.id.primary;
      TextView primary = ViewBindings.findChildViewById(rootView, id);
      if (primary == null) {
        break missingId;
      }

      id = R.id.rating_bar;
      RatingBar ratingBar = ViewBindings.findChildViewById(rootView, id);
      if (ratingBar == null) {
        break missingId;
      }

      id = R.id.row_two;
      LinearLayout rowTwo = ViewBindings.findChildViewById(rootView, id);
      if (rowTwo == null) {
        break missingId;
      }

      id = R.id.secondary;
      TextView secondary = ViewBindings.findChildViewById(rootView, id);
      if (secondary == null) {
        break missingId;
      }

      return new GntMediumTemplateViewBinding(rootView, adNotificationView, body, cta, headline,
          icon, mediaView, nativeAdView, primary, ratingBar, rowTwo, secondary);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}