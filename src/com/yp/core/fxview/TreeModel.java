package com.yp.core.fxview;

import java.util.List;

import com.yp.core.tools.ITree;

import javafx.scene.control.TreeItem;

public class TreeModel {

	private TreeModel() {
		super();
	}

	public static TreeItem<ITree<?>> buildTreeNode(final List<? extends ITree<?>> pList, final int pIndis) {
		if (pIndis < pList.size()) {
			final ITree<?> parent = pList.get(pIndis);
			final TreeItem<ITree<?>> mParentNode = new TreeItem<>(parent);
			final Object parentValue = parent.getValue();
			for (int i = pIndis + 1; i < pList.size(); ++i) {
				final ITree<?> de = pList.get(i);
				TreeItem<ITree<?>> mNode;
				if (!de.isLeaf()) {
					mNode = buildTreeNode(pList, i);
				} else {
					mNode = new TreeItem<>(de);
				}
				if (de.getParentValue().equals(parentValue) && mNode != null) {
					mParentNode.getChildren().add(mNode);
				}
			}
			return mParentNode;
		}
		return null;
	}

	public static TreeItem<ITree<?>> getTreeViewItem(final TreeItem<ITree<?>> item, final ITree<?> value) {
		if (item != null) {
			if (item.getValue().equals(value)) {
				return item;
			}
			for (final TreeItem<ITree<?>> child : item.getChildren()) {
				final TreeItem<ITree<?>> s = getTreeViewItem(child, value);
				if (s != null) {
					return s;
				}
			}
		}
		return null;
	}
}