package logic.ltl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import automata.safa.SAFAInputMove;
import automata.safa.booleanexpression.SumOfProducts;
import theory.BooleanAlgebra;

public class Until<P, S> extends LTLFormula<P, S> {

	protected LTLFormula<P, S> left, right;

	public Until(LTLFormula<P, S> left, LTLFormula<P, S> right) {
		super();
		this.left = left;
		this.right = right;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Until))
			return false;
		@SuppressWarnings("unchecked")
		Until<P, S> other = (Until<P, S>) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}

	@Override
	protected void accumulateSAFAStatesTransitions(HashMap<LTLFormula<P, S>, Integer> formulaToStateId,
			HashMap<Integer, Collection<SAFAInputMove<P, S>>> moves,
			Collection<Integer> finalStates, BooleanAlgebra<P, S> ba) {

		// If I already visited avoid recomputing
		if (formulaToStateId.containsKey(this))
			return;

		// Update hash tables
		int id = formulaToStateId.size();
		formulaToStateId.put(this, id);

		// Compute transitions for children
		left.accumulateSAFAStatesTransitions(formulaToStateId, moves, finalStates, ba);
		right.accumulateSAFAStatesTransitions(formulaToStateId, moves, finalStates, ba);

		// delta(l U r, p) = delta(l, p) and lUr
		// delta(l U r, p) = delta(r, p)
		int leftId = formulaToStateId.get(left);
		int rightId = formulaToStateId.get(right);
		Collection<SAFAInputMove<P, S>> leftMoves = moves.get(leftId);
		Collection<SAFAInputMove<P, S>> rightMoves = moves.get(rightId);
		Collection<SAFAInputMove<P, S>> newMoves = new LinkedList<>();
		for (SAFAInputMove<P, S> leftMove : leftMoves)
			newMoves.add(new SAFAInputMove<P, S>(id, leftMove.to.and(new SumOfProducts(id)), leftMove.guard));

		for (SAFAInputMove<P, S> rightMove : rightMoves)
			newMoves.add(new SAFAInputMove<P, S>(id, rightMove.to, rightMove.guard));

		moves.put(id, newMoves);
	}

	@Override
	protected boolean isFinalState() {
		return false;
	}

}
