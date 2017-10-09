package com.example.apollinariia.litup.sensors;

/**
 * Created by Apollinariia on 10/9/2017.
 */
import com.mbientlab.metawear.AsyncDataProducer;
import com.mbientlab.metawear.ConfigEditorBase;
import com.mbientlab.metawear.Configurable;
import com.mbientlab.metawear.MetaWearBoard.Module;
import com.mbientlab.metawear.data.AngularVelocity;
import com.mbientlab.metawear.module.GyroBmi160;

import java.util.HashMap;

/**
 * Sensor on the BMI160 IMU measuring angular velocity
 * @author Eric Tsai
 */
public interface Gyroscope extends Module, Configurable<GyroBmi160.ConfigEditor> {
    /**
     * Operating frequency of the gyro
     * @author Eric Tsai
     */
    enum OutputDataRate {
        /** 25Hz */
        ODR_25_HZ,
        /** 50Hz */
        ODR_50_HZ,
        /** 100Hz */
        ODR_100_HZ,
        /** 200Hz */
        ODR_200_HZ,
        /** 400Hz */
        ODR_400_HZ,
        /** 800Hz */
        ODR_800_HZ,
        /** 1600Hz */
        ODR_1600_HZ,
        /** 3200Hz */
        ODR_3200_HZ;

        public final byte bitmask;

        OutputDataRate() {
            this.bitmask= (byte) (ordinal() + 6);
        }
    }
    /**
     * Supported angular rate measurement range
     * @author Eric Tsai
     */
    enum Range {
        /** +/- 2000 degrees / second */
        FSR_2000(16.4f),
        /** +/- 1000 degrees / second */
        FSR_1000(32.8f),
        /** +/- 500 degrees / second */
        FSR_500(65.6f),
        /** +/- 250 degrees / second */
        FSR_250(131.2f),
        /** +/- 125 degrees / second */
        FSR_125(262.4f);

        public final float scale;
        public final byte bitmask;

        Range(float scale) {
            this.scale= scale;
            this.bitmask= (byte) ordinal();
        }

        private static final HashMap<Byte, Range> bitMaskToRanges;
        static {
            bitMaskToRanges= new HashMap<>();
            for(Range it: Range.values()) {
                bitMaskToRanges.put(it.bitmask, it);
            }
        }
        public static Range bitMaskToRange(byte mask) {
            return bitMaskToRanges.get(mask);
        }
    }
    /**
     * Interface to configure parameters for measuring angular velocity
     * @author Eric Tsai
     */
    interface ConfigEditor extends ConfigEditorBase {
        /**
         * Set the measurement range
         * @param range    New range to use
         * @return Calling object
         */
        ConfigEditor range(Range range);
        /**
         * Set the output date rate
         * @param odr    New output data rate to use
         * @return Calling object
         */
        ConfigEditor odr(OutputDataRate odr);
    }

    /**
     * Reports measured angular velocity values from the gyro.  Combined XYZ data is represented as an
     * {@link AngularVelocity} object while split data is interpreted as a float.
     * @author Eric Tsai
     */
    interface AngularVelocityDataProducer extends AsyncDataProducer {
        /**
         * Get the name for x-axis data
         * @return X-axis data name
         */
        String xAxisName();
        /**
         * Get the name for y-axis data
         * @return Y-axis data name
         */
        String yAxisName();
        /**
         * Get the name for z-axis data
         * @return Z-axis data name
         */
        String zAxisName();
    }
    /**
     * Get an implementation of the AngularVelocityDataProducer interface
     * @return AngularVelocityDataProducer object
     */
    AngularVelocityDataProducer angularVelocity();
    /**
     * Variant of angular velocity data that packs multiple data samples into 1 BLE packet to increase the
     * data throughput.  Only streaming is supported for this data producer.
     * @return Object representing packed acceleration data
     */
    AsyncDataProducer packedAngularVelocity();

    /**
     * Starts the gyo
     */
    void start();
    /**
     * Stops the gyo
     */
    void stop();
}
