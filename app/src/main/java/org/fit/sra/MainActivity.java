package org.fit.sra;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;
import org.fit.sra.service.PermissionService;
import org.fit.sra.service.SoundClassifierService;
import org.fit.sra.ui.RecognitionHistoryFragment;
import org.fit.sra.ui.SettingsFragment;
import org.fit.sra.ui.SoundClassifierFragment;

public class MainActivity
    extends
    AppCompatActivity
    implements
    NavigationView.OnNavigationItemSelectedListener,
    SoundClassifierFragment.ClassifierProvider {

  private DrawerLayout drawerLayout;
  private PermissionService permissionService;
  private SoundClassifierService classifierService;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    drawerLayout = findViewById(R.id.drawer_layout);
    NavigationView navigationView = findViewById(R.id.navigation_view);
    navigationView.setNavigationItemSelectedListener(this);

    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawerLayout, toolbar,
        R.string.navigation_drawer_open,
        R.string.navigation_drawer_close
    );
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

    // Initialize Services
    permissionService = new PermissionService(this);
    if (permissionService.hasPermissionNotGranted()) {
      permissionService.requestAllPermissions();
    }

    classifierService = new SoundClassifierService(this);

    // Load default fragment
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.content_frame, new SoundClassifierFragment())
        .commit();
  }

  /**
   * Routing condition
   *
   * @param item The selected item
   * @return boolean
   */
  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    Fragment selected = null;

    // use if else instead of switch bc Android Team said so
    // https://web.archive.org/web/20230203152426/http://tools.android.com/tips/non-constant-fields
    int itemId = item.getItemId();

    if (itemId == R.id.nav_sound_classifier) {
      selected = new SoundClassifierFragment();
    } else if (itemId == R.id.nav_recognition_history) {
      selected = new RecognitionHistoryFragment();
    } else if (itemId == R.id.nav_settings) {
      selected = new SettingsFragment();
    }

    if (selected != null) {
      getSupportFragmentManager()
          .beginTransaction()
          .replace(R.id.content_frame, selected)
          .commit();
    }

    drawerLayout.closeDrawer(GravityCompat.START);
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  // Provide Services to Fragment
  @Override
  public PermissionService getPermissionService() {
    return permissionService;
  }

  @Override
  public SoundClassifierService getSoundClassifierService() {
    return classifierService;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
      @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    permissionService.handlePermissionsResult(requestCode, permissions, grantResults);
  }
}
