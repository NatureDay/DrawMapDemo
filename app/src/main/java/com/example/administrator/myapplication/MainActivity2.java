package com.example.administrator.myapplication;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.TilesOverlay;

import java.util.ArrayList;

/**
 * Author: Administrator
 * Time: 2017/4/18 17:04
 * m：路线图
 * t：地形图
 * p：带标签的地形图
 * s：卫星图
 * y：带标签的卫星图
 * h：标签层（路名、地名等）
 */
public class MainActivity2 extends AppCompatActivity implements PolygonView.onDrawFinishListener {

    private MapView mMapView;
    private PolygonView mPolygonView;

    public static final OnlineTileSourceBase GoogleSat = new XYTileSource("Google-Sat",
            0, 20, 256, ".png", new String[]{
//            "http://mt0.google.com",
//            "http://mt1.google.com",
//            "http://mt2.google.com",
//            "http://mt3.google.com"
            "http://www.google.cn/maps"
    }) {
        @Override
        public String getTileURLString(MapTile aTile) {
//            return getBaseUrl() + "/vt/lyrs=y&hl=zh-CN&gl=cn&x=" + aTile.getX() + "&y=" + aTile.getY() + "&z=" + aTile.getZoomLevel();

            // http://www.google.cn/maps  PC上使用
//            return getBaseUrl() + "/vt/lyrs=y@189&gl=cn&x=" + aTile.getX() + "&y=" + aTile.getY() + "&z=" + aTile.getZoomLevel();

//            http://www.google.cn/maps/vt?lyrs=s@729&gl=cn&x=434469&y=214609&z=19
            // http://www.google.cn/maps  手机上使用
            return getBaseUrl() + "/vt/lyrs=h@729&gl=cn&x=" + aTile.getX() + "&y=" + aTile.getY() + "&z=" + aTile.getZoomLevel();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_main2);

        mMapView = (MapView) findViewById(R.id.map);
        mMapView.setTileSource(GoogleSat);
        mMapView.setBuiltInZoomControls(true);
        mMapView.getController().setZoom(15);
        /**
         * 图片随着dpi变。。。。。。
         */
        mMapView.setTilesScaledToDpi(true);
        mMapView.setMaxZoomLevel(20);
        mMapView.setMinZoomLevel(5);
        GeoPoint point = new GeoPoint(31.3234634, 118.3631643);
        mMapView.getController().setCenter(point);

        MapTileProviderBasic tileProvider = new MapTileProviderBasic(getApplicationContext());
        tileProvider.setTileSource(GoogleSat);
        TilesOverlay tilesOverlay = new TilesOverlay(tileProvider,getApplicationContext());

        mMapView.getOverlays().add(tilesOverlay);

        mPolygonView = (PolygonView) findViewById(R.id.drawView);
        mPolygonView.setVisibility(View.VISIBLE);
        mPolygonView.setOnDrawFinishListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }

    @Override
    public void onDrawFinish(ArrayList<Point> points) {
        mPolygonView.setVisibility(View.GONE);
        Projection projection = mMapView.getProjection();
        ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
        for (int i = 0; i < points.size(); i++) {
            GeoPoint geoPoint = (GeoPoint) projection.fromPixels(points.get(i).x, points.get(i).y);
            geoPoints.add(geoPoint);
            Log.e("fff", "------pixelsPoint--XX==" + points.get(i).x + "---------YY==" + points.get(i).y);
            Point newPoint = projection.toPixels(geoPoint, null);
            Log.e("fff", "------geoPoints--XX==" + newPoint.x + "---------YY==" + newPoint.y);
        }
        Polygon polygon = new Polygon();
        polygon.setPoints(geoPoints);
        polygon.setFillColor(0x44FF0000);
        polygon.setStrokeColor(0);
        mMapView.getOverlayManager().add(polygon);
        mMapView.invalidate();
    }

    @Override
    protected void onDestroy() {
        if (mMapView != null) {
            mMapView.onDetach();
        }
        super.onDestroy();
    }
}
