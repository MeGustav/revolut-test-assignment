package com.megustav.revolut.module;

import com.google.inject.AbstractModule;
import com.megustav.revolut.misc.BlockingService;
import com.megustav.revolut.misc.impl.BlockingServiceImpl;

/**
 * Module providing miscellaneous beans
 *
 * @author MeGustav
 * 26/04/2018 20:01
 */
public class MiscModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(BlockingService.class).to(BlockingServiceImpl.class);
    }

}
