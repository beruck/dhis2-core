/*
 * Copyright (c) 2004-2021, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis;

import lombok.extern.slf4j.Slf4j;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.hisp.dhis.config.IntegrationTestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

/*
 * @author Gintare Vilkelyte <vilkelyte.gintare@gmail.com>
 */
@ExtendWith( SpringExtension.class )
@ContextConfiguration( classes = { IntegrationTestConfig.class } )
@IntegrationTest
@ActiveProfiles( profiles = { "test-postgres" } )
@Transactional
@Slf4j
public abstract class TransactionalIntegrationTest extends BaseSpringTest
{
    @BeforeEach
    public final void before()
        throws Exception
    {
        integrationTestBefore();
    }

    @AfterEach
    public final void after()
        throws Exception
    {
        clearSecurityContext();

        tearDownTest();

        try
        {
            dbmsManager.clearSession();
        }
        catch ( Exception e )
        {
            log.info( "Failed to clear hibernate session, reason:" + e.getMessage() );
        }

        if ( emptyDatabaseAfterTest() )
        {
            // We normally don't want all the delete/empty db statements in the
            // query logger
            Configurator.setLevel( "org.hisp.dhis.datasource.query", Level.WARN );

            try
            {
                dbmsManager.emptyDatabase();
            }
            catch ( Exception e )
            {
                log.info( "Failed to empty db, reason:" + e.getMessage() );
            }
        }
    }

    protected boolean emptyDatabaseAfterTest()
    {
        return true;
    }
}
