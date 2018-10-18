package com.pms.component.ganttchart;

import org.tltv.gantt.client.shared.Step;

public interface GanttListener {

    void stepModified(Step step);

    void stepDeleted(Step step);
}
