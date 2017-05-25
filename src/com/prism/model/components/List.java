package com.prism.model.components;

import java.awt.Graphics2D;
import java.util.HashMap;

import com.prism.model.Bounds;
import com.prism.model.Color;
import com.prism.model.Component;
import com.prism.model.Location.Hook;
import com.prism.model.ReactiveText;
import com.prism.model.ThingList;
import com.prism.model.Value;
import com.prism.model.components.Label.Align;

public class List<T> extends Component {

	private ListProcessor<T> processor;
	private ListSorter<T> sorter;
	private ThingList<Component> children;
	private HashMap<T, Component> components;
	private Panel panel;
	private Scroller scroller;

	public List(Bounds bounds, ListProcessor<T> processor) {
		super(bounds, BLANK_STYLE);
		this.processor  = processor;
		this.children   = new ThingList<Component>(this);
		this.components = new HashMap<T, Component>();
		this.scroller   = new Scroller(bounds);
		this.panel = new Panel(new Bounds() {

			@Override
			public float getWidth() {
				return List.this.width();
			}

			@Override
			public float getHeight() {
				return children.isEmpty() ? 0F : children.height();
			}

		});
		//this.panel.style(Panel.BACKGROUND, Color.parse(0, 0, 0, 0));
		this.scroller.locate(this, Hook.C);
		this.scroller.scroll(panel);
		panel.translate(0F, -3F);
		attach(scroller);
	}

	public List(Bounds bounds) {
		this(bounds, null);
	}

	public interface ListProcessor<T> {
		public Component getComponent(T element, List<T> container);
	}

	public interface ListSorter<T> {
		public boolean isGreaterThan(T alpha, T beta);
	}

	public class GenericProcessor implements ListProcessor<T> {

		@Override
		public Component getComponent(T element, final List<T> container) {
			Bounds bounds = new Bounds.Respective(new Value() {
				@Override
				public float getValue() {
					return container.width();
				}
			}, 50F);
			Panel panel = new Panel(bounds);
			Label label = new Label(bounds, new ReactiveText(element));
			label.style(Label.COLOR, Color.parse(255, 255, 255));
			label.style(Label.ALIGN,   Align.MIDDLE);
			label.style(Label.V_ALIGN, Align.MIDDLE);
			label.locate(panel, Hook.C);
			panel.style(Panel.BACKGROUND, Color.random());
			//panel.attach(label);
			return panel;
		}

	}

	public void add(T element) {
		if(processor == null) {
			processor = new GenericProcessor();
		}
		Component component = processor.getComponent(element, this);
		components.put(element, component);
		if(children.isEmpty()) {
			component.locate(panel, Hook.NW);
		} else {
			Component prior = children.get(children.size() - 1);
			component.locate(Hook.NW, prior, Hook.SW);
		}
		component.clip(this);
		children.add(component);
		panel.attach(component);
	}

	public void resort() {

	}

	public void remove(T element) {

	}

	public Component getComponent(T element) {
		return components.get(element);
	}


	@Override
	public int getMouseCursor(float x, float y) {
		return -1;
	}

	@Override
	public void hover(float x, float y) {
	}

	@Override
	public void draw(Graphics2D graphics, int width, int height) {
		graphics.clip(null);
	}

	@Override
	public void overlay(Graphics2D graphics, int width, int height) {
		graphics.drawRect(0, 0, width, height);
	}

	@Override
	public void update(int width, int height, Graphics2D graphics) {
	}

	@Override
	public boolean mouseEvent(float x, float y, int type, boolean control,
			boolean shift) {
		return false;
	}

	@Override
	public boolean keyEvent(int key, boolean release, String text,
			boolean control, boolean shift) {
		return false;
	}

	@Override
	public boolean dragged(int x, int y) {
		return false;
	}

	@Override
	public void state(int state) {
	}

	public ListProcessor<T> getProcessor() {
		return processor;
	}

	public void setProcessor(ListProcessor<T> processor) {
		this.processor = processor;
	}

	public ListSorter<T> getSorter() {
		return sorter;
	}

	public void setSorter(ListSorter<T> sorter) {
		this.sorter = sorter;
	}



}
