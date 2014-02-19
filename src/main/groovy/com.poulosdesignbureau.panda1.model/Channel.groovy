package com.poulosdesignbureau.panda1.model

/**
 *
 * @author lore
 */
class Channel {
    
    static final String CHANNEL_CONST = "Channel #"
    static final String TYPE_CONST = "Type"
    static final String DESC_CONST = "Description"
    static final String DETAILS_CONST = "Details"
    static final String SERIAL_CONST = "Serial Number"
    static final String HEIGHT_CONST = "Height"
    static final String SCALE_CONST = "Scale Factor"
    static final String OFFSET_CONST = "Offset"
    static final String UNITS_CONST = "Units"
    
    String channel
    String type
    String description
    String details
    String serialNumber
    String height
    double scaleFactor
    double offset
    String units
    
    String toString() {
        return """
                   $CHANNEL_CONST: $channel\n\
                   $TYPE_CONST: $type\n\
                   $DESC_CONST: $description\n\
                   $DETAILS_CONST: $details\n\
                   $SERIAL_CONST: $serialNumber\n\
                   $HEIGHT_CONST: $height\n\
                   $SCALE_CONST: $scaleFactor\n\
                   $OFFSET_CONST: $offset\n\
                   $UNITS_CONST: $units
               """
    }
}

