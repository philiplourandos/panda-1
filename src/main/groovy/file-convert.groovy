import com.poulosdesignbureau.panda1.model.Channel

import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern

import org.apache.commons.cli.Option
import org.apache.commons.io.FileUtils

def cli = new CliBuilder(usage: "groovy file-convert.groovy --input \$dir/input --completed \$dir/completed --generated \$dir/generated")
cli.with {
    c longOpt: 'completed', 'Directory where processed files are moved into.', type: String, required: false, args: 1
    i longOpt: 'input', 'Input directory of files that require processing.', type: String, required: false, args: 1
    o longOpt: 'outputFile', 'Directory and filename to output generated content to.', type: String, required: false, args: 1
}

def opts = cli.parse(args)

if(!opts) {
    cli.usage()
    
    return
}

String inputDir = opts.i
if(!inputDir) {
    inputDir = 'input'
}

String completeDir = opts.c
if(!completeDir) {
    completeDir = 'completed'
}

String outputFile = opts.o
if(!outputFile) {
    println "No '-outputFile' specified."
    
    return
}

File input = new File(inputDir)

File generatedOutputFile = new File(outputFile)
File genParentPath = generatedOutputFile.getParentFile();
if(!genParentPath.exists()) {
    genParentPath.mkdirs()
}
File completed = new File(completeDir)
if(!completed.exists()) {
    completed.mkdirs()
}

Map<String, Channel> channelInfo

int headerLength = 0
def output = new StringBuilder(6000)
def match 

def sortedFiles = input.listFiles().sort() { it.name }
sortedFiles.each {
    
    def currentFileName = it.getName()
    
    println "Processing file: $currentFileName"
    
    String fileContent = it.text

    boolean processSingleHeader = true
    
    if(channelInfo == null) {
        channelInfo = new HashMap<String, Channel>();

        String startValue = '-----Sensor Information-----'
        int start = fileContent.indexOf(startValue) + startValue.length()
        int end = fileContent.indexOf('Date & Time Stamp')
        String headerInfo = fileContent.substring(start, end);

        String[] channelLines = headerInfo.split(System.getProperty("line.separator"))

        Channel currentChannel

        println 'Start: Mapping header.'
        
        channelLines.each { line -> 
            if(line.startsWith(Channel.CHANNEL_CONST)) {
                String channelNumber = line.replaceAll(Channel.CHANNEL_CONST, "").trim();

                if(channelInfo.containsKey(channelNumber)) {
                    currentChannel = channelInfo[channelNumber]
                } else {
                    currentChannel = new Channel()
                    channelInfo[channelNumber] = currentChannel
                }

                currentChannel.channel = channelNumber
            } else if(line.startsWith(Channel.TYPE_CONST)) {
                currentChannel.type = line.replaceAll(Channel.TYPE_CONST, "").trim()
            } else if(line.startsWith(Channel.DESC_CONST)) {
                currentChannel.description = line.replaceAll(Channel.DESC_CONST, "").trim()
            } else if(line.startsWith(Channel.DETAILS_CONST)) {
                currentChannel.details = line.replaceAll(Channel.DESC_CONST, "").trim()
            } else if(line.startsWith(Channel.SERIAL_CONST)) {
                currentChannel.serialNumber = line.replaceAll(Channel.SERIAL_CONST, "").trim()
            } else if(line.startsWith(Channel.HEIGHT_CONST)) {
                currentChannel.height = line.replaceAll(Channel.HEIGHT_CONST, "").trim()
            } else if(line.startsWith(Channel.SCALE_CONST)) {
                currentChannel.scaleFactor = Double.valueOf(line.replaceAll(Channel.SCALE_CONST, "").trim())
            } else if(line.startsWith(Channel.OFFSET_CONST)) {
                currentChannel.offset = Double.valueOf(line.replaceAll(Channel.OFFSET_CONST, "").trim())
            } else if(line.startsWith(Channel.UNITS_CONST)) {
                currentChannel.units = line.replaceAll(Channel.UNITS_CONST, "").trim()
            }
        }

        match = (fileContent =~ /Date.*\r\n/)
        headerLength = match[0].size()

        def rowHeaders

        if(headerLength != 0) {
            rowHeaders = match[0].split("\t")
        } else {
            println "No header information for file: $it"

            System.exit(0)
        }
    
        rowHeaders.each { header ->
            output << header.trim() << ','
        }
    
        println 'Start: Adding units for headers'
        
        output << "\n"
        rowHeaders.each { header ->
            def headerMatcher = (header =~ /\w\w(\d{1,3})/)
            if(headerMatcher.find()) {
                String[] values = headerMatcher[0]
            
                Channel requiredChannel = channelInfo[values[1]]
                if(requiredChannel != null) {
                    output << requiredChannel.units << ','
                } else {
                    output << ','
                }
            } else {
                output << ','
            }
        }
        output << "\n"
        
        println 'End: Adding units for headers'
    }
    
    int startDataIndex = fileContent.indexOf(match[0]) + headerLength
    String rawData = fileContent.substring(startDataIndex)

    String[] dataRows = rawData.split("\r\n")
    
    dataRows.each { line ->
        String[] values = line.split("\t")

        values.each {
            output << it << ','
        }
        
        output << "\n"
    }
    
    File completedFile = new File(completed, it.getName())

    println "Moving input file: " + it.getPath() + " to " + completedFile.getPath()

    FileUtils.moveFile(it, completedFile)
}

println "Writing processed data to file: $generatedOutputFile"
generatedOutputFile << output.toString()
