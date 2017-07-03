package org.kurron.avro.example

import org.apache.avro.file.DataFileWriter
import org.apache.avro.specific.SpecificDatumWriter

import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Should run before the tests, outputting this schema's data to a file.
 */
class DatFileWriter {

    static final dataFileLocation = '../v200.bin'
    // due to rounding of seconds, we hard code the time
    static final now = LocalDateTime.now( Clock.fixed( Instant.EPOCH, ZoneId.systemDefault() ) )
    static final date = now.toLocalDate().toEpochDay() as int
    static final time = now.toLocalTime().toSecondOfDay() * 1000
    public static final String FIRST_NAME = 'firstname-v200'
    public static final String LAST_NAME = 'lastname-v200'
    public static final String USER_NAME = 'username-v200'
    public static final boolean ACTIVE = true
    public static final int ID = Integer.MAX_VALUE
    public static final Gender GENDER = Gender.FEMALE
    public static final String BREAKING_CHANGE = 'breakingChange-v200'
    public static final String COMMENT = 'Reset password v200'
    public static final String SESSION_KEY = 'May'
    public static final int SESSION_VALUE = 200

    static void main(String[] args) {
        def encoded = User.newBuilder().setFirstname(FIRST_NAME)
                .setLastname(LAST_NAME)
                .setUsername(USER_NAME)
                .setActive(ACTIVE)
                .setId(ID)
                .setAddedDate(date)
                .setAddedTime(time)
                .setGender(GENDER)
                .setBreakingChange(BREAKING_CHANGE)
                .build()
        encoded.comments.add(COMMENT)
        encoded.sessions[SESSION_KEY] = SESSION_VALUE

        def datumWriter = new SpecificDatumWriter<User>( User )
        def dataFileWriter = new DataFileWriter<User>( datumWriter )

        dataFileWriter.create( encoded.getSchema(), new File( dataFileLocation ) )
        dataFileWriter.append( encoded )
        dataFileWriter.flush()
        dataFileWriter.close()
    }
}
