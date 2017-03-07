package jp.hichain.prototype.algorithm;

import java.util.EnumMap;

import jp.hichain.prototype.basic.Square;
import jp.hichain.prototype.concept.AroundDir;
import jp.hichain.prototype.concept.AroundDir.Axis;
import jp.hichain.prototype.concept.Direction.Relation;

public class AroundSearcher {
	public static void set(Square root) {
		EnumMap <AroundDir, Square> arounds = root.getAroundAll();
		for (AroundDir dir : AroundDir.values()) {
			Square target = arounds.get(dir);
			if (target != null) {
				continue;
			}

			int vertical = dir.getComp(Axis.VERTICAL);
			int horizontal = dir.getComp(Axis.HORIZONTAL);
			Square around = search(root, vertical, horizontal);
			if (around == null) {
				around = new Square(root, dir);
				System.out.println("New Square: " + dir);
			}
			root.addAround(dir, around);
			around.addAround(dir.get(Relation.OPPOSITE), root);
			System.out.println("Hit Square: " + dir);
		}
	}

	private static Square search(Square root, int v, int h) {
		if (v == 0 && h == 0) {
			return root;
		}

		int firstDirV = (v == 0) ? v : (v / (int)Math.abs(v));
		int firstDirH = (h == 0) ? h : (h / (int)Math.abs(h));
		AroundDir first = AroundDir.getByComp(firstDirV, firstDirH);

		AroundDir targetL = first.get(Relation.LEFT);
		AroundDir targetR = first.get(Relation.RIGHT);
		AroundDir target = targetL;
		for (int i = 0; target == first.get(Relation.OPPOSITE); i++) {
			target = (i%2 == 0) ? targetL : targetR;

			Square next = root.getAround(target);
			if (next != null) {
				int targetV = target.getComp(Axis.VERTICAL);
				int targetH = target.getComp(Axis.HORIZONTAL);
				Square square = search(root.getAround(target), v-targetV, h-targetH);
				if (square != null) {
					return square;
				}
			}

			if (i%2 == 0) {
				targetL = targetL.get(Relation.LEFT);
			} else {
				targetR = targetR.get(Relation.RIGHT);
			}
		}

		return null;
	}
}
