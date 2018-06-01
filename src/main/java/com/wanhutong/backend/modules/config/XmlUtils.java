package com.wanhutong.backend.modules.config;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import com.wanhutong.backend.modules.config.parse.PaymentOrderProcessConfig;
import com.wanhutong.backend.modules.config.parse.PurchaseOrderProcessConfig;
import com.wanhutong.backend.modules.config.parse.RequestOrderProcessConfig;
import com.wanhutong.backend.modules.config.parse.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Before use {@code XmlUtils} by annotations, add processAnnotations on the
 * annotated class in static block.
 * 
 */
public class XmlUtils {

	private static final Logger logger = LoggerFactory.getLogger(XmlUtils.class);

	private static final XStream X_STREAM = new XStream(new Xpp3Driver()) {

		@Override
		protected MapperWrapper wrapMapper(MapperWrapper next) {
			return new MapperWrapper(next) {

				@Override
				public boolean shouldSerializeMember(Class definedIn, String fieldName) {
					if (definedIn == Object.class) {
						logger.warn("Unknown field in the xml, {}", fieldName);
						return false;
					} else {
						return super.shouldSerializeMember(definedIn, fieldName);
					}
				}
			};
		}
	}; 

	static {
		X_STREAM.processAnnotations(PurchaseOrderProcessConfig.class);
		X_STREAM.processAnnotations(RequestOrderProcessConfig.class);
		X_STREAM.processAnnotations(PaymentOrderProcessConfig.class);
		X_STREAM.processAnnotations(SystemConfig.class);
	}

	@SuppressWarnings("unchecked")
	public static <T> T fromXml(String content) {
		return (T) X_STREAM.fromXML(content);
	}

}
