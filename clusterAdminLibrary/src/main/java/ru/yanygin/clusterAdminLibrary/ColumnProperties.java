package ru.yanygin.clusterAdminLibrary;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ColumnProperties {
	
	@SerializedName("Order")
	@Expose
	public int[] order = null;
	
	@SerializedName("Width")
	@Expose
	public int[] width = null;
	
	@SerializedName("Visible")
	@Expose
	public boolean[] visible = null;

	public ColumnProperties(int size) {
		
		// Порядок столбцов
		this.order = new int[size];
		for (int i = 0; i < this.order.length; i++)
			this.order[i] = i;

		// Ширина столбцов
		this.width = new int[size];
		
		// Видимость столбцов
		this.visible = new boolean[size];
		for (int i = 0; i < this.visible.length; i++)
			this.visible[i] = true;
	}
	
	public void updateColumnProperties(int arraySize) {
		
		// если после десериализации количество не равно текущему
		// (например, поменялся состав колонок),
		// нужно переложить в новый массив без потерь
		
		// Порядок столбцов
		if (order.length != arraySize) {
			int[] columnOrderTemp = order;
			order = new int[arraySize];
			System.arraycopy(columnOrderTemp, 0, order, 0,
					Math.min(arraySize, columnOrderTemp.length));
			
			if (arraySize > columnOrderTemp.length)
				for (int i = columnOrderTemp.length; i < arraySize; i++)
					order[i] = i;
		}
		
		// Ширина столбцов
		if (width.length != arraySize) {
			int[] columnWidthTemp = width;
			width = new int[arraySize];
			System.arraycopy(columnWidthTemp, 0, width, 0,
					Math.min(arraySize, columnWidthTemp.length));
		}
		
		// Видимость столбцов
		if (visible.length != arraySize) {
			boolean[] columnVisibleTemp = visible;
			visible = new boolean[arraySize];
			System.arraycopy(columnVisibleTemp, 0, visible, 0,
					Math.min(arraySize, columnVisibleTemp.length));
			if (arraySize > columnVisibleTemp.length)
				for (int i = columnVisibleTemp.length; i < arraySize; i++)
					visible[i] = true;
		}
		
	}

}
