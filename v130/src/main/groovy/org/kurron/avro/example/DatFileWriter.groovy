package org.kurron.avro.example

import org.apache.avro.file.DataFileWriter
import org.apache.avro.specific.SpecificDatumWriter

import java.nio.ByteBuffer
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Should run before the tests, outputting this schema's data to a file.
 */
class DatFileWriter {

    static final DATA_FILE_LOCATION = '../v130.bin'
    // due to rounding of seconds, we hard code the time
    static final NOW = LocalDateTime.now( Clock.fixed( Instant.EPOCH, ZoneId.systemDefault() ) )
    static final DATE = Math.toIntExact( NOW.toLocalDate().toEpochDay() )
    static final TIME = Math.toIntExact( NOW.toLocalTime().toSecondOfDay() )
    static final INT_TO_LONG = Integer.MAX_VALUE
    static final STRING_TO_BYTES = LocalDateTime.now().toLocalDate().toString()
    static final BYTES_TO_STRING = LocalDateTime.now().toLocalDate().toString().getBytes( 'UTF-8')
    public static final String FIRST_NAME = 'firstname-v130'
    public static final String LAST_NAME = 'lastname-v130'
    public static final String USERNAME = 'username-v130'
    public static final boolean ACTIVE = true
    public static final int ID = Integer.MAX_VALUE
    public static final String COMMENT = 'Reset password v130'
    public static final String SESSION_KEY = 'May'
    public static final int SESSION_VALUE = 130
    public static final Gender GENDER = Gender.FEMALE

    static void main(String[] args) {
        def promotionExample = PromotionExample.newBuilder()
                                               .setIntToLong( INT_TO_LONG )
                                               .setStringToBytes( STRING_TO_BYTES )
                                               .setBytesToString(ByteBuffer.wrap( BYTES_TO_STRING ) )
                                               .build()
        def encoded = User.newBuilder()
                          .setFirstname(FIRST_NAME)
                          .setLastname(LAST_NAME)
                          .setUsername(USERNAME)
                          .setActive(ACTIVE)
                          .setId(ID)
                          .setAddedDate( DATE )
                          .setAddedTime( TIME )
                          .setGender(GENDER)
                          .setPromotionExample( promotionExample )
                          .build()
        encoded.comments.add(COMMENT)
        encoded.sessions[SESSION_KEY] = SESSION_VALUE

        def datumWriter = new SpecificDatumWriter<User>( User )
        def dataFileWriter = new DataFileWriter<User>( datumWriter )

        dataFileWriter.create( encoded.getSchema(), new File( DATA_FILE_LOCATION ) )
        dataFileWriter.append( encoded )
        dataFileWriter.flush()
        dataFileWriter.close()
    }
}
