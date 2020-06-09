package edu.iis.mto.testreactor.dishwasher;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import edu.iis.mto.testreactor.dishwasher.engine.Engine;
import edu.iis.mto.testreactor.dishwasher.engine.EngineException;
import edu.iis.mto.testreactor.dishwasher.pump.PumpException;
import edu.iis.mto.testreactor.dishwasher.pump.WaterPump;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

public class DishWasherTest {

    private WaterPump waterPump = Mockito.mock(WaterPump.class);
    private Engine engine = Mockito.mock(Engine.class);
    private DirtFilter dirtFilter = Mockito.mock(DirtFilter.class);
    private Door door = Mockito.mock(Door.class);
    private DishWasher dishWasher;
    private final double correctFilterCapacity = 100d;
    private final double incorrectFilterCapacity = 30d;
    private final int chosenProgrammeDurationMinutes = 90;

    @Before public void setUp() throws Exception {
        dishWasher = new DishWasher(waterPump, engine, dirtFilter, door);
    }

    @Test public void correctDishWashingShouldResultInSuccess() {
        when(door.closed()).thenReturn(true);
        when(dirtFilter.capacity()).thenReturn(correctFilterCapacity);
        RunResult runResult = dishWasher.start(createProgramConfiguration(true));
        RunResult expectedResult = RunResult.builder().withStatus(Status.SUCCESS).withRunMinutes(chosenProgrammeDurationMinutes).build();
        assertEquals(expectedResult.getStatus(), runResult.getStatus());
        assertEquals(expectedResult.getRunMinutes(), runResult.getRunMinutes());
    }

    @Test public void ifDoorIsOpenThenStatusShouldBeErrorDoorOpen() {
        when(door.closed()).thenReturn(false);
        when(dirtFilter.capacity()).thenReturn(correctFilterCapacity);
        RunResult runResult = dishWasher.start(createProgramConfiguration(true));
        RunResult expectedResult = RunResult.builder().withStatus(Status.DOOR_OPEN).withRunMinutes(chosenProgrammeDurationMinutes).build();
        assertEquals(expectedResult.getStatus(), runResult.getStatus());
    }

    @Test public void ifFilterIsNotCleanShouldReturnErrorFilter() {
        when(door.closed()).thenReturn(true);
        when(dirtFilter.capacity()).thenReturn(incorrectFilterCapacity);
        RunResult runResult = dishWasher.start(createProgramConfiguration(true));
        RunResult expectedResult = RunResult.builder().withStatus(Status.ERROR_FILTER).withRunMinutes(chosenProgrammeDurationMinutes).build();
        assertEquals(expectedResult.getStatus(), runResult.getStatus());
    }

    @Test public void filterShouldBeCleanIfTabletsNotUsed() {
        when(door.closed()).thenReturn(true);
        when(dirtFilter.capacity()).thenReturn(correctFilterCapacity);
        dishWasher.start(createProgramConfiguration(false));
        verify(dirtFilter, never()).capacity();
    }
    @Test public void engineExceptionShouldResultInErrorProgram() throws EngineException {
        when(door.closed()).thenReturn(true);
        doThrow(EngineException.class).when(engine).runProgram(any());
        when(dirtFilter.capacity()).thenReturn(correctFilterCapacity);
        RunResult runResult = dishWasher.start(createProgramConfiguration(true));
        assertEquals(Status.ERROR_PROGRAM, runResult.getStatus());
    }
    @Test public void pumpExceptionShouldResultInErrorPump() throws PumpException {
        when(door.closed()).thenReturn(true);
        doThrow(PumpException.class).when(waterPump).drain();
        when(dirtFilter.capacity()).thenReturn(correctFilterCapacity);
        RunResult runResult = dishWasher.start(createProgramConfiguration(true));
        assertEquals(Status.ERROR_PUMP, runResult.getStatus());
    }
    @Test public void correctWashingShouldCallDoorAndWaterPumpAndEngineInCorrectOrder() throws PumpException, EngineException {
        when(door.closed()).thenReturn(true);
        when(dirtFilter.capacity()).thenReturn(correctFilterCapacity);
        dishWasher.start(createProgramConfiguration(true));
        InOrder inOrder = inOrder(door, waterPump, engine);
        inOrder.verify(door).closed();
        inOrder.verify(door).lock();
        inOrder.verify(waterPump).pour(any());
        inOrder.verify(engine).runProgram(any());
        inOrder.verify(waterPump).drain();
    }
    private ProgramConfiguration createProgramConfiguration(boolean tabletsUsed) {
        return ProgramConfiguration.builder()
                                   .withFillLevel(FillLevel.FULL)
                                   .withProgram(WashingProgram.ECO)
                                   .withTabletsUsed(tabletsUsed)
                                   .build();
    }

}
