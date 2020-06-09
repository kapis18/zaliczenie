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
    private final double correctFilterCapacity = 100d;
    private final double incorrectFilterCapacity = 30d;

    @Before public void setUp() throws Exception {
        dishWasher = new DishWasher(waterPump, engine, dirtFilter, door);
    }

    @Test public void correctDishWashingShouldResultInSuccess() {
        when(door.closed()).thenReturn(true);
        when(dirtFilter.capacity()).thenReturn(correctFilterCapacity);
        RunResult runResult = dishWasher.start(createProgramConfiguration());
        RunResult expectedResult = RunResult.builder().withStatus(Status.SUCCESS).withRunMinutes(90).build();
        assertEquals(expectedResult.getStatus(), runResult.getStatus());
        assertEquals(expectedResult.getRunMinutes(), runResult.getRunMinutes());
    }

    @Test public void ifDoorIsOpenThenStatusShouldBeErrorDoorOpen() {
        when(door.closed()).thenReturn(false);
        when(dirtFilter.capacity()).thenReturn(correctFilterCapacity);
        RunResult runResult = dishWasher.start(createProgramConfiguration());
        RunResult expectedResult = RunResult.builder().withStatus(Status.DOOR_OPEN).withRunMinutes(90).build();
        assertEquals(expectedResult.getStatus(), runResult.getStatus());
    }
    @Test public void ifFilterIsNotCleanShouldReturnErrorFilter(){
        when(door.closed()).thenReturn(true);
        when(dirtFilter.capacity()).thenReturn(incorrectFilterCapacity);
        RunResult runResult = dishWasher.start(createProgramConfiguration());
        RunResult expectedResult = RunResult.builder().withStatus(Status.ERROR_FILTER).withRunMinutes(90).build();
        assertEquals(expectedResult.getStatus(), runResult.getStatus());
    }

    private ProgramConfiguration createProgramConfiguration() {
        return ProgramConfiguration.builder()
                                   .withFillLevel(FillLevel.FULL)
                                   .withProgram(WashingProgram.ECO)
                                   .withTabletsUsed(true)
                                   .build();
    }
}
