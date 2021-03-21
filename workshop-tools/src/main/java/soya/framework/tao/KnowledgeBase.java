package soya.framework.tao;

/**
 * <p>
 * 道可道也，非恒道也。名可名也，非恒名也。
 * 无名，万物之始也；有名，万物之母也。
 * 故恒无欲也，以观其眇；恒有欲也，以观其所徼。两者同出，异名同谓。玄之又玄，众眇之门。
 * </p>
 * <p>
 * T: 宇宙的本原和实质，或事物的客观存在，即无名。
 * K: 对宇宙或事物的认知，即有名；认知是可以深化和改变的。
 * </p>
 *
 * @author Wen Qun
 */
public interface KnowledgeBase<T, K extends Annotatable> {
    T tao();

    K knowledge();
}
