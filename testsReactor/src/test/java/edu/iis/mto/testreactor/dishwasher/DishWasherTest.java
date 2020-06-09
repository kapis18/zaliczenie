package edu.iis.mto.testreactor.dishwasher;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import edu.iis.mto.testreactor.dishwasher.engine.Engine;
import edu.iis.mto.testreactor.dishwasher.pump.WaterPump;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class DishWasherTest {

    private WaterPump waterPump = Mockito.mock(WaterPump.class);
    private Engine engine = Mockito.mock(Engine.class);
    private DirtFilter dirtFilter = Mockito.mock(DirtFilter.class);
    private Door door = Mockito.mock(Door.class);

    private DishWasher dishWasher;
    @Before public void setUp() throws Exception {
        dishWasher = new DishWasher(waterPump, engine, dirtFilter, door);
    }
    @Test
    public void correctDishWashingShouldResultInSuccess() {
        ProgramConfiguration programConfiguration = ProgramConfiguration.builder()
                                                                        .withFillLevel(FillLevel.FULL)
                                                                        .withProgram(WashingProgram.ECO)
                                                                        .withTabletsUsed(true)
                                                                        .build();
        when(door.closed()).thenReturn(true);
        when(dirtFilter.capacity()).thenReturn(100d);
        RunResult runResult = dishWasher.start(programConfiguration);
        RunResult expectedResult = RunResult.builder().withStatus(Status.SUCCESS).withRunMinutes(90).build();
        assertEquals(expectedResult.getStatus(), runResult.getStatus());
        assertEquals(expectedResult.getRunMinutes(), runResult.getRunMinutes());
    }

}
