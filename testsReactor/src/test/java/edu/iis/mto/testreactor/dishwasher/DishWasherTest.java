package edu.iis.mto.testreactor.dishwasher;

import static org.hamcrest.MatcherAssert.assertThat;

import edu.iis.mto.testreactor.dishwasher.engine.Engine;
import edu.iis.mto.testreactor.dishwasher.pump.WaterPump;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

class DishWasherTest {

    @Mock WaterPump waterPump;
    @Mock Engine engine;
    @Mock DirtFilter dirtFilter;
    @Mock Door door;

    private DishWasher dishWasher;
    @Before public void setUp() throws Exception {
        dishWasher = new DishWasher(waterPump, engine, dirtFilter, door);
    }
    @Test
    public void itCompiles() {
        assertThat(true, Matchers.equalTo(true));
    }

}
