package org.sbolbin.crpt.concurrent;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class CacheableFunctionExecutorTest {

    private static final int KEY = 1;
    private static final int EXPECTED_RESULT = 2;

    private final Function<Integer, Integer> functionMock =  mock(Function.class);
    private final Function<Integer, Integer> otherFunctionMock =  mock(Function.class);
    private final CacheableFunctionExecutor<Integer, Integer> executor = new CacheableFunctionExecutor<>();

    @AfterEach
    public void afterEach() {
        reset(functionMock, otherFunctionMock);
    }

    @Test
    @DisplayName("No value in cache for the key - key is calculated")
    public void test_1() throws Exception {
        //setup
        when(functionMock.apply(KEY)).thenReturn(EXPECTED_RESULT);

        //act
        Future<Integer> result = executor.calculate(KEY, functionMock);

        //verify
        assertEquals(EXPECTED_RESULT, result.get());
        verify(functionMock, times(1)).apply(KEY);
    }

    @Test
    @DisplayName("Value present in the cache - taken from the cache")
    public void test_2() throws Exception {
        //setup
        when(functionMock.apply(KEY)).thenReturn(EXPECTED_RESULT);
        executor.calculate(KEY, functionMock).get(); //calculate - put value into the cache
        reset(functionMock);

        //act
        Integer result = executor.calculate(KEY, functionMock).get(); //calculate again - expect, that value taken from the cache

        //verify
        assertEquals(EXPECTED_RESULT, result);
        verifyNoInteractions(functionMock);
    }

    @Test
    @DisplayName("Exception occurred during cache calculation - ExecutionException thrown on GET")
    public void test_3() {
        //setup
        when(functionMock.apply(KEY)).thenThrow(new RuntimeException("Ex"));

        //act
        Future<Integer> future = executor.calculate(KEY, functionMock);

        //verify
        assertThrows(ExecutionException.class, future::get);
        verify(functionMock, times(1)).apply(KEY);
    }

    @Test
    @DisplayName("Cached value returned even another function requested")
    public void test_4() throws Exception {
        //setup
        when(functionMock.apply(KEY)).thenReturn(EXPECTED_RESULT);
        when(otherFunctionMock.apply(KEY)).thenReturn(100);

        executor.calculate(KEY, functionMock).get(); //put value into the cache

        //act
        Integer result = executor.calculate(KEY, otherFunctionMock).get();

        //verify
        assertEquals(EXPECTED_RESULT, result); //cached value returned
        verify(functionMock, times(1)).apply(KEY);
        verifyNoInteractions(otherFunctionMock); // another function not invoked
    }

    @Test
    @DisplayName("Key is null - NullPointerException thrown")
    public void test_5() {
        assertThrows(NullPointerException.class, () -> executor.calculate(null, functionMock));
    }

    @Test
    @DisplayName("Check calculation of two different keys")
    public void test_6() throws Exception {
        //setup
        Integer otherKey = 2;
        Integer otherResult = 100;
        when(functionMock.apply(KEY)).thenReturn(EXPECTED_RESULT);
        when(otherFunctionMock.apply(otherKey)).thenReturn(otherResult);

        //act
        Integer result1 = executor.calculate(KEY, functionMock).get();
        Integer result2 = executor.calculate(otherKey, otherFunctionMock).get();

        //verify
        assertEquals(EXPECTED_RESULT, result1);
        assertEquals(otherResult, result2);

        verify(functionMock, times(1)).apply(KEY);
        verify(otherFunctionMock, times(1)).apply(otherKey);
    }
}