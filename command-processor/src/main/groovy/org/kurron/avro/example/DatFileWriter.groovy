package org.kurron.avro.example

import org.apache.avro.file.DataFileWriter
import org.apache.avro.specific.SpecificDatumWriter

/**
 * Should run before the tests, outputting this schema's data to a file.
 */
class DatFileWriter {

    static final DATA_FILE_LOCATION = '../v100.bin'
    static final String NAME = 'name-v100'

    static void main(String[] args) {

        def encoded = User.newBuilder().setName(NAME).build()
        def datumWriter = new SpecificDatumWriter<User>( User )
        def dataFileWriter = new DataFileWriter<User>( datumWriter )
        dataFileWriter.create( encoded.getSchema(), new File( DATA_FILE_LOCATION ) )
        dataFileWriter.append( encoded )
        dataFileWriter.flush()
        dataFileWriter.close()
    }
}
