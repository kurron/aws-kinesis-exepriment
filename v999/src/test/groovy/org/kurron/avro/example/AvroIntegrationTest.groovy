package org.kurron.avro.example

import org.apache.avro.Schema
import org.apache.avro.file.DataFileReader
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.generic.GenericRecord
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Exercises Avro codec.
 */
class AvroIntegrationTest extends Specification {

    @Unroll
    def 'Reading data from #dataFile using schema #schemaFile'() {

        expect:
        def schema = new Schema.Parser().parse( new File("../${schemaFile}/src/main/resources/schema/user.json" ) )
        def file = new File( "../${dataFile}.bin" )
        def datumReader = new GenericDatumReader<GenericRecord>( schema )
        def dataFileReader = new DataFileReader<GenericRecord>( file, datumReader )
        def records = dataFileReader.collect { it }
        dataFileReader.close()
        records.every { expectation.call( it ) }

        where:
        dataFile | schemaFile || expectation
        'v110'   | 'v100'     || { GenericRecord record -> record.get( 'name' ) }

        'v120'   | 'v100'     || { GenericRecord record -> record.get( 'name' ) }
        'v120'   | 'v110'     || { GenericRecord record -> record.get( 'username' ) }

        'v130'   | 'v100'     || { GenericRecord record -> record.get( 'name' ) }
        'v130'   | 'v110'     || { GenericRecord record -> record.get( 'username' ) }
        'v130'   | 'v120'     || { GenericRecord record -> record.get( 'lastname' ) }

        'v140'   | 'v100'     || { GenericRecord record -> record.get( 'name' ) }
        'v140'   | 'v110'     || { GenericRecord record -> record.get( 'username' ) }
        'v140'   | 'v120'     || { GenericRecord record -> record.get( 'lastname' ) }
        'v140'   | 'v130'     || { GenericRecord record -> record.get( 'promotion error' ) }
    }

}
