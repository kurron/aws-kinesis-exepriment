package org.kurron.avro.example

import org.apache.avro.file.DataFileWriter
import org.apache.avro.specific.SpecificDatumWriter

/**
 * Should run before the tests, outputting this schema's data to a file.
 */
class DatFileWriter {

    static final DATA_FILE_LOCATION = '../v120.bin'
    static final String FIRST_NAME = 'firstname-v120'
    static final String LAST_NAME = 'lastname-v120'
    static final String USERNAME = 'username-v120'

    static void main(String[] args) {
        def encoded = User.newBuilder()
                          .setFirstname(FIRST_NAME)
                          .setLastname(LAST_NAME)
                          .setUsername(USERNAME).build()
        def datumWriter = new SpecificDatumWriter<User>( User )
        def dataFileWriter = new DataFileWriter<User>( datumWriter )
        dataFileWriter.create( encoded.getSchema(), new File( DATA_FILE_LOCATION ) )
        dataFileWriter.append( encoded )
        dataFileWriter.flush()
        dataFileWriter.close()

    }
}
