/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.shared.kerberos.codec.encryptionKey;


import org.apache.directory.shared.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.shared.asn1.ber.grammar.Grammar;
import org.apache.directory.shared.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.KerberosConstants;
import org.apache.directory.shared.kerberos.codec.actions.CheckNotNullLength;
import org.apache.directory.shared.kerberos.codec.encryptionKey.actions.EncryptionKeyInit;
import org.apache.directory.shared.kerberos.codec.encryptionKey.actions.EncryptionKeyKeyType;
import org.apache.directory.shared.kerberos.codec.encryptionKey.actions.EncryptionKeyKeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements the EncryptionKey structure. All the actions are declared
 * in this class. As it is a singleton, these declaration are only done once. If
 * an action is to be added or modified, this is where the work is to be done !
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class EncryptionKeyGrammar extends AbstractGrammar
{
    /** The logger */
    static final Logger LOG = LoggerFactory.getLogger( EncryptionKeyGrammar.class );

    /** A speedup for logger */
    static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /** The instance of grammar. EncryptionKeyGrammar is a singleton */
    private static Grammar instance = new EncryptionKeyGrammar();


    /**
     * Creates a new EncryptionKeyGrammar object.
     */
    private EncryptionKeyGrammar()
    {
        setName( EncryptionKeyGrammar.class.getName() );

        // Create the transitions table
        super.transitions = new GrammarTransition[EncryptionKeyStatesEnum.LAST_ENCKEY_STATE.ordinal()][256];

        // ============================================================================================
        // PaData 
        // ============================================================================================
        // --------------------------------------------------------------------------------------------
        // Transition from EncryptionKey init to EncryptionKey SEQ OF
        // --------------------------------------------------------------------------------------------
        // EncryptionKey         ::= SEQUENCE {
        super.transitions[EncryptionKeyStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(
            EncryptionKeyStatesEnum.START_STATE, EncryptionKeyStatesEnum.ENCKEY_SEQ_STATE, UniversalTag.SEQUENCE.getValue(),
            new EncryptionKeyInit() );
        
        // --------------------------------------------------------------------------------------------
        // Transition from EncryptionKey SEQ to padata-type tag
        // --------------------------------------------------------------------------------------------
        // EncryptionKey         ::= SEQUENCE {
        //       keytype     [0]
        super.transitions[EncryptionKeyStatesEnum.ENCKEY_SEQ_STATE.ordinal()][KerberosConstants.ENCRYPTION_KEY_TYPE_TAG] = new GrammarTransition(
            EncryptionKeyStatesEnum.ENCKEY_SEQ_STATE, EncryptionKeyStatesEnum.ENCKEY_TYPE_TAG_STATE, KerberosConstants.ENCRYPTION_KEY_TYPE_TAG,
            new CheckNotNullLength() );
        
        // --------------------------------------------------------------------------------------------
        // Transition from EncryptionKey type tag to padata-type
        // --------------------------------------------------------------------------------------------
        // EncryptionKey         ::= SEQUENCE {
        //       keytype     [1] Int32
        super.transitions[EncryptionKeyStatesEnum.ENCKEY_TYPE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(
            EncryptionKeyStatesEnum.ENCKEY_TYPE_TAG_STATE, EncryptionKeyStatesEnum.ENCKEY_TYPE_STATE, UniversalTag.INTEGER.getValue(),
            new EncryptionKeyKeyType() );
        
        // --------------------------------------------------------------------------------------------
        // Transition from padata-type to padata-value tag
        // --------------------------------------------------------------------------------------------
        // EncryptionKey         ::= SEQUENCE {
        //          keyvalue    [2]
        super.transitions[EncryptionKeyStatesEnum.ENCKEY_TYPE_STATE.ordinal()][KerberosConstants.ENCRYPTION_KEY_VALUE_TAG] = new GrammarTransition(
            EncryptionKeyStatesEnum.ENCKEY_TYPE_STATE, EncryptionKeyStatesEnum.ENCKEY_VALUE_TAG_STATE, KerberosConstants.ENCRYPTION_KEY_VALUE_TAG,
            new CheckNotNullLength() );
        
        // --------------------------------------------------------------------------------------------
        // Transition from padata-value tag to padata-value
        // --------------------------------------------------------------------------------------------
        // EncryptionKey         ::= SEQUENCE {
        //          keyvalue    [2] OCTET STRING
        super.transitions[EncryptionKeyStatesEnum.ENCKEY_VALUE_TAG_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(
            EncryptionKeyStatesEnum.ENCKEY_VALUE_TAG_STATE, EncryptionKeyStatesEnum.ENCKEY_VALUE_STATE, UniversalTag.OCTET_STRING.getValue(),
            new EncryptionKeyKeyValue() );
    }


    // ~ Methods
    // ------------------------------------------------------------------------------------

    /**
     * Get the instance of this grammar
     * 
     * @return An instance on the EncryptionKey Grammar
     */
    public static Grammar getInstance()
    {
        return instance;
    }
}