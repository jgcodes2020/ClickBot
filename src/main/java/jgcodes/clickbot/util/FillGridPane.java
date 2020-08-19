package jgcodes.clickbot.util;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class FillGridPane extends GridPane {
  public static void setFillAll(Node child) {
    GridPane.setFillWidth(child, true);
    GridPane.setFillWidth(child, true);
  }
  public static void setGrowAlways(Node child) {
    GridPane.setHgrow(child, Priority.ALWAYS);
    GridPane.setVgrow(child, Priority.ALWAYS);
  }

  @Override
  public void add(Node child, int columnIndex, int rowIndex) {
    if (child instanceof Region) {
      Region region = (Region) child;
      region.setMaxWidth(Double.MAX_VALUE);
      region.setMaxHeight(Double.MAX_VALUE);
    }
    FillGridPane.setFillAll(child);
    FillGridPane.setGrowAlways(child);
    super.add(child, columnIndex, rowIndex);
  }

  @Override
  public void add(Node child, int columnIndex, int rowIndex, int colspan, int rowspan) {
    if (child instanceof Region) {
      Region region = (Region) child;
      region.setMaxWidth(Double.MAX_VALUE);
      region.setMaxHeight(Double.MAX_VALUE);
    }
    FillGridPane.setFillAll(child);
    FillGridPane.setGrowAlways(child);
    super.add(child, columnIndex, rowIndex, colspan, rowspan);
  }

  @Override
  public void addRow(int rowIndex, Node... children) {
    for (Node child: children) {
      if (child instanceof Region) {
        Region region = (Region) child;
        region.setMaxWidth(Double.MAX_VALUE);
        region.setMaxHeight(Double.MAX_VALUE);
      }
      FillGridPane.setFillAll(child);
      FillGridPane.setGrowAlways(child);
    }
    super.addRow(rowIndex, children);
  }

  @Override
  public void addColumn(int columnIndex, Node... children) {
    for (Node child: children) {
      if (child instanceof Region) {
        Region region = (Region) child;
        region.setMaxWidth(Double.MAX_VALUE);
        region.setMaxHeight(Double.MAX_VALUE);
      }
      FillGridPane.setFillAll(child);
      FillGridPane.setGrowAlways(child);
    }
    super.addColumn(columnIndex, children);
  }
}
