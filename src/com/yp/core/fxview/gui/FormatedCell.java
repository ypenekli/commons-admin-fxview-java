package com.yp.core.fxview.gui;

import java.text.Format;
import java.time.LocalDate;

import com.yp.core.entity.IDataEntity;
import com.yp.core.tools.DateTime;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

public class FormatedCell<S, T> implements Callback<TableColumn<IDataEntity, T>, TableCell<IDataEntity, T>> {

	private TextAlignment alignment;
	private Format format;
	private boolean editable;
	
	public TextAlignment getAlignment() {
		if (alignment == null)
			alignment = TextAlignment.LEFT;
		return alignment;
	}

	public void setAlignment(TextAlignment alignment) {
		this.alignment = alignment;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean pEditable) {
		editable = pEditable;
	}

	@Override
	@SuppressWarnings("unchecked")
	public TableCell<IDataEntity, T> call(TableColumn<IDataEntity, T> p) {
		TableCell<IDataEntity, T> cell = new TableCell<IDataEntity, T>() {
			@Override
			public void updateItem(Object item, boolean empty) {
				if (item == getItem()) {
					return;
				}
				super.updateItem((T) item, empty);
				if (item == null) {
					super.setText(null);
					super.setGraphic(null);
				} else if (format != null) {
					if (item instanceof LocalDate)
						super.setText(format.format(DateTime.asDate((LocalDate) item)));
					else super.setText(format.format(item));
				} else if (item instanceof Node) {
					super.setText(null);
					super.setGraphic((Node) item);
				} else {
					super.setText(item.toString());
					super.setGraphic(null);
				}
			}
		};
		cell.setTextAlignment(getAlignment());
		switch (alignment) {
		case CENTER:
			cell.setAlignment(Pos.CENTER);
			break;
		case RIGHT:
			cell.setAlignment(Pos.CENTER_RIGHT);
			break;
		default:
			cell.setAlignment(Pos.CENTER_LEFT);
			break;
		}
		cell.setEditable(isEditable());
		return cell;
	}
}