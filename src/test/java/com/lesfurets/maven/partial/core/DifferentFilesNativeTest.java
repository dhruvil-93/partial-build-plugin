package com.lesfurets.maven.partial.core;

import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Guice;
import com.lesfurets.maven.partial.mocks.ModuleMock;

@RunWith(MockitoJUnitRunner.class)
public class DifferentFilesNativeTest extends DifferentFilesTest {

    protected DifferentFiles getInstance() throws Exception {
        return Guice.createInjector(ModuleMock.module(getLocalRepoMock().getRepoDir().toString()))
                .getInstance(DifferentFilesNative.class);
    }

}
