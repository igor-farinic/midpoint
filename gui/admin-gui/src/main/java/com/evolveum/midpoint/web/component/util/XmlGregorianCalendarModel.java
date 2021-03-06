/*
 * Copyright (c) 2010-2013 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evolveum.midpoint.web.component.util;

import com.evolveum.midpoint.util.MiscUtil;
import org.apache.commons.lang.time.DateUtils;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Calendar;
import java.util.Date;

/**
 * @author lazyman
 */
public class XmlGregorianCalendarModel extends Model<Date> {

    private IModel<XMLGregorianCalendar> model;
    private boolean copyTime;

    public XmlGregorianCalendarModel(IModel<XMLGregorianCalendar> model) {
        this(model, true);
    }

    public XmlGregorianCalendarModel(IModel<XMLGregorianCalendar> model, boolean copyTime) {
        this.model = model;
        this.copyTime = copyTime;
    }

    @Override
    public Date getObject() {
        XMLGregorianCalendar calendar = model.getObject();
        if (calendar == null) {
            return null;
        }
        return MiscUtil.asDate(calendar);
    }

    @Override
    public void setObject(Date object) {
        if (object == null) {
            model.setObject(null);
        } else {
            if (copyTime) {
                Date d = getObject();
                object = copyTime(d, object);
            }
            model.setObject(MiscUtil.asXMLGregorianCalendar(object));
        }
    }

    private Date copyTime(Date from, Date to) {
        if (from == null || to == null) {
            return to;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);

        to = DateUtils.setHours(to, calendar.get(Calendar.HOUR_OF_DAY));
        to = DateUtils.setMinutes(to, calendar.get(Calendar.MINUTE));
        to = DateUtils.setSeconds(to, calendar.get(Calendar.SECOND));
        to = DateUtils.setMilliseconds(to, calendar.get(Calendar.MILLISECOND));

        return to;
    }
}
