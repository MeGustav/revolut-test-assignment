package com.megustav.revolut.module;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.megustav.revolut.rest.handler.AccountsManagementHandler;
import com.megustav.revolut.rest.handler.AccountsOperationsHandler;
import com.megustav.revolut.rest.handler.Handler;
import com.megustav.revolut.rest.handler.ManagementHandler;

/**
 * Module providing handlers
 *
 * @author MeGustav
 * 26/04/2018 20:01
 */
public class HandlersModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<Handler> binder = Multibinder.newSetBinder(binder(), Handler.class);
        binder.addBinding().to(ManagementHandler.class);
        binder.addBinding().to(AccountsManagementHandler.class);
        binder.addBinding().to(AccountsOperationsHandler.class);
    }

}
