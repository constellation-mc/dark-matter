package me.melontini.dark_matter.test.base.config;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.EqualsAndHashCode;
import me.melontini.dark_matter.api.base.config.ConfigManager;
import me.melontini.dark_matter.api.base.util.Context;
import net.fabricmc.loader.api.FabricLoader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConfigManagerTest {

  private static ConfigManager<TestCfg> MANAGER;

  @BeforeAll
  public static void setupConfigManager() {
    MANAGER = ConfigManager.of(TestCfg.class, "dark-matter/test_cfg");
  }

  @AfterAll
  public static void cleanUpConfigManager() throws IOException {
    Files.deleteIfExists(MANAGER.resolve(FabricLoader.getInstance().getConfigDir()));
    MANAGER = null;
  }

  @Test
  @Order(1)
  public void testConfigProperties() {
    assertThat(MANAGER.type()).isEqualTo(TestCfg.class);
    assertThat(MANAGER.name()).isEqualTo("dark-matter/test_cfg");
    assertThat(MANAGER.createDefault()).isEqualTo(new TestCfg());
  }

  @Test
  @Order(2)
  public void testCreateLoad() {
    AtomicBoolean atomic = new AtomicBoolean(false);
    MANAGER.onLoad((config, path) -> atomic.set(true));
    MANAGER.load(FabricLoader.getInstance().getConfigDir(), Context.of());
    Assertions.assertThat(atomic).isTrue();
  }

  @Test
  @Order(3)
  public void testCreateSave() {
    TestCfg cfg = MANAGER.createDefault();
    cfg.testInt = 34;

    AtomicBoolean atomic = new AtomicBoolean(false);
    MANAGER.onSave((config, path) -> atomic.set(true));
    MANAGER.save(FabricLoader.getInstance().getConfigDir(), cfg, Context.of());
    Assertions.assertThat(atomic).isTrue();
  }

  @Test
  @Order(4)
  public void testReloadSaved() {
    TestCfg cfg = MANAGER.load(FabricLoader.getInstance().getConfigDir(), Context.of());
    Assertions.assertThat(cfg).hasFieldOrPropertyWithValue("testInt", 34);
  }

  @Test
  public void testThrowsOnAccessDenied() {
    var manager = ConfigManager.of(PrivateCfg.class, "dark-matter/private");
    Assertions.assertThatThrownBy(manager::createDefault)
        .hasMessage("Failed to construct " + PrivateCfg.class.getName())
        .isInstanceOf(RuntimeException.class);
  }

  private static final class PrivateCfg {
    public List<Integer> ints = List.of(1, 22);
  }

  @EqualsAndHashCode
  public static final class TestCfg {
    public boolean testValue = true;
    public int testInt = 420;
  }
}
