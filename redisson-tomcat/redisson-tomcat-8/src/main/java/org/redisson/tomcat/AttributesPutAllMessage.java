/**
 * Copyright (c) 2013-2024 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.redisson.tomcat;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

/**
 * 
 * @author Nikita Koksharov
 *
 */
public class AttributesPutAllMessage extends AttributeMessage {

    private Map<String, byte[]> attrs;
    
    public AttributesPutAllMessage() {
    }

    public AttributesPutAllMessage(String nodeId, String sessionId, Map<String, Object> attrs, Encoder encoder) throws Exception {
        super(nodeId, sessionId);
        if (attrs != null) {
            this.attrs = new HashMap<>();
            for (Entry<String, Object> entry: attrs.entrySet()) {
                Object value = entry.getValue();
                try {
                    if (value instanceof Collection) {
                        Collection newInstance = (Collection) value.getClass().getDeclaredConstructor().newInstance();
                        newInstance.addAll((Collection) value);
                        value = newInstance;
                    }
                    if (value instanceof Map) {
                        Map newInstance = (Map) value.getClass().getDeclaredConstructor().newInstance();
                        newInstance.putAll((Map) value);
                        value = newInstance;
                    }
                } catch (Exception e) {
                    // can't be copied
                }
                this.attrs.put(entry.getKey(), toByteArray(encoder, value));
            }
        } else {
            this.attrs = null;
        }
    }

    public Map<String, Object> getAttrs(Decoder<?> decoder) throws IOException, ClassNotFoundException {
    	if (attrs == null) {
    		return null;
    	}
    	Map<String, Object> result = new HashMap<String, Object>();
    	for (Entry<String, byte[]> entry: attrs.entrySet()) {
    		result.put(entry.getKey(), toObject(decoder, entry.getValue()));
    	}
        return result;
    }

}
